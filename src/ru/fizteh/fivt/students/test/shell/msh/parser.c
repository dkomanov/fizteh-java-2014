#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <memory.h>
#include <stdio.h>
#include "term_helper.h"
#include "mgets.h"
#include "param.h"
#include "merr.h"
#include "parser.h"

void init_job_parse(struct job_parse *x)
{
    x->ischar = 0;
    x->j = NULL;
    x->k = 0;
    x->cur = NULL;
    x->state = 0;
    x->was_space = 0;
}

void initjob(struct job *x)
{
    x->args = NULL;
    x->argv = 0;
    x->redout = NULL;
    x->redin = NULL;
    x->next = NULL;
    x->bg = 0;
    x->out_append = 0;
}
char misalpha(char c)
{
    return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c == '_') || (c == '?') || (c >= '0' && c <= '1');
}


char* push_back(char *s, char c)
{
    int n;
    char *q;
    if (s== NULL)
        n = 0;
    else
        n = strlen(s);
    q = realloc(s, n + 2);
    if (q == NULL)
    {
        free(s);
        seterr(ERR_MEM);
        return NULL;
    }
    q[n] = c;
    q[n + 1] = 0;
    return q;
}

char* initstr()
{
    char *c = malloc(1);
    if (c != NULL)
    {
        c[0] = 0;
        seterr(ERR_MEM);
    }
    return c;
}

/*
 * state:
 * 0 - start
 * 1 - conv
 * 2 - none
 * 3 - arg
 * 4 - inp
 * 5 - outp
 * 6 -
 */

int insertchar(struct job_parse *x, char c)
{
    x->ischar = 1;
    if (x->state == 0)
    {
        struct job* temp = realloc(x->j, (x->k + 1) * sizeof(struct job));
        if (temp == NULL)
        {
            seterr(ERR_MEM);
            return -1;
        }
        x->j = temp;
        x->cur = x->j + x->k;
        initjob(x->cur);
        x->k++;
        x->state = 2;
    }
    if (x->state == 1)
    {
        x->cur->next = malloc(sizeof(struct job));
        if (x->cur->next == NULL)
        {
            seterr(ERR_MEM);
            return -1;
        }
        x->cur = x->cur->next;
        initjob(x->cur);
        x->state = 2;
    }
    if (x->state == 2)
    {
        char** mtemp = realloc(x->cur->args, (x->cur->argv + 1) * sizeof(char*));
        if (mtemp == NULL)
        {
            seterr(ERR_MEM);
            return -1;
        }
        x->cur->args = mtemp;
        x->cur->args[x->cur->argv] = malloc(1);
        if (x->cur->args[x->cur->argv] == NULL)
        {
            seterr(ERR_MEM);
            return -1;
        }
        x->cur->args[x->cur->argv][0] = 0;
        x->cur->argv++;
        x->state = 3;
    }
    if (x->state == 3)
    {
        x->cur->args[x->cur->argv - 1] = push_back(x->cur->args[x->cur->argv - 1], c);
        if (x->cur->args[x->cur->argv - 1] == NULL)
            return -1;
        return 0;
    }
    if (x->state == 4)
    {
        x->cur->redin = push_back(x->cur->redin, c);
        if (x->cur->redin == NULL)
            return -1;
        return 0;
    }
    if (x->state == 5)
    {
        x->cur->redout = push_back(x->cur->redout, c);
        if (x->cur->redout == NULL)
            return -1;
        return 0;
    }
    seterr(ERR_PARSE);
    return -1;
}

int insertspec(struct job_parse *x, char c)
{
    if (c == ' ')
        x->was_space = 1;
    else x->was_space = 0;
    if (c == ' ' && x->ischar)
        x->state = 2;
    else if (c == ' ' && !x->ischar)
        ;
    else if (c == '<')
    {
        x->was_space = 0;
        if (x->state == 0 || x->state == 1 || x->cur->redin != NULL || x->state == 4)
        {
            seterr(ERR_PARSE_REDIN);
            return -1;
        }
        x->state = 4;
        x->ischar = 0;
    }
    else if (c == '>')
    {
        if (!x->was_space && x->state==5 && x->ischar == 0 && !x->j[x->k - 1].out_append)
        {
            x->cur->out_append = 1;
        }
        else if (x->state == 0 || x->state == 1 || x->state == 5  || x->cur->redout != NULL)
        {
            seterr(ERR_PARSE_REDOUT);
            return -1;
        }
        else
        {
            x->state = 5;
            x->ischar = 0;
        }
    }
    else if (x->state == 4 && x->cur->redin == NULL)
    {
        seterr(ERR_PARSE_REDIN);
        return -1;
    }
    else if (x -> state == 5 && x->cur->redout == NULL)
    {
        seterr(ERR_PARSE_REDOUT);
        return -1;
    }
    else if (c == ';')
    {
        if (x->state == 0 || x->state == 1)
        {
            seterr(ERR_PARSE_FG);
            return -1;
        }
        x->j[x->k - 1].bg = 2;
        x->state = 0;
        x->ischar = 0;
    }
    else if (c == '|')
    {
        if (!x->was_space && x->state == 1)
        {
            x->state = 0;
            x->j[x->k - 1].bg = 3;
        }
        else if (x->state == 0 || x->state == 1)
        {
            seterr(ERR_PARSE_CONV);
            return -1;
        }
        else
        {
            x->state = 1;
            x->ischar = 0;
        }
    }
    else if (c == '&')
    {
        if (!x->was_space && x->state == 0 && x->k > 0 && x->j[x->k - 1].bg == 1)
        {
            x->j[x->k - 1].bg = 4;
        }
        else if (x->state == 0 || x->state == 1)
        {
            seterr(ERR_PARSE_BG);
            return -1;
        }
        else
        {
            x->state = 0;
            x->ischar = 0;
            x->j[x->k - 1].bg = 1;
        }
    }
    return 0;
}

void jobfree(struct job *x)
{
    int i;
    if (x->redin != NULL)
        free(x->redin);
    if (x->redout != NULL)
        free(x->redout);
    if (x->next != NULL)
    {
        jobfree(x->next);
        free(x->next);
    }
    for (i = 0; i < x->argv; ++i)
        free(x->args[i]);
    free(x->args);
}

void allfree(struct job_parse *x, char *s)
{
    int i;
    if (s != NULL)
        free(s);
    for (i = 0; i < x->k; ++i)
        jobfree(&x->j[i]);
    free(x->j);
}

void parsefree(struct job *x, int k)
{
    int i;
    for (i = 0; i < k; ++i)
        jobfree(&x[i]);
    free(x);
}

char get_spec(char c)
{
    if (c == 'n')
        return '\n';
    return c;
}


int parse(int file, struct job** res)
{
    char *s;
    char isslash, isquot, is2quot, isparam, isignore, isbrack;
    int i, n, j;
    struct job_parse p;
    char *param;
    char finished;
    finished = 0;
    s = NULL;
    isslash = isquot = is2quot = isparam = isignore = isbrack = 0;
    init_job_parse(&p);
    while (!finished)
    {
        if (s != NULL)
        {
            free(s);
            if (is_interactive)
                write(file, "\x1B[1;31m > \x1B[0m", 14);
        }
        else if (is_interactive)
            write(file, "\x1B[1;31mmsh > \x1B[0m", 17);
        s = mgets(file);
        if (s == NULL)
        {
            allfree(&p, s);
            seterr(ERR_PARSE_END_FILE);
            return -2;
        }
        n = strlen(s);
        isignore = 0;
        for (i = 0; i < n; ++i)
        {
            if (isignore)
                continue;
            if (isparam)
            {
                if (param[0] == 0 && s[i] == '{')
                {
                    isbrack = 1;
                    continue;
                }
                else if (misalpha(s[i]) || (isbrack && s[i] != '}'))
                {
                    param = push_back(param, s[i]);
                    if (param == NULL)
                    {
                        allfree(&p, s);
                        return -1;
                    }
                    continue;
                }
                else
                {
                    char *rparam = getparam(param);
                    if (rparam == NULL)
                    {
                        free(param);
                        allfree(&p, s);
                        return -1;
                    }
                    for (j = 0; j < strlen(rparam); ++j)
                    {
                        if (insertchar(&p, rparam[j]))
                        {
                            allfree(&p, s);
                            return -1;
                        }
                    }
                    free(param);
                    isparam = 0;
                }
                if (isbrack && s[i] == '}')
                {
                    isbrack = 0;
                    continue;
                }
            }
            if (isslash)
            {
                isslash = 0;
                if (insertchar(&p, s[i]))
                {
                    allfree(&p, s);
                    return -1;
                }
            }
            else if (isquot)
            {
                if (s[i] == '\'')
                    isquot = 0;
                else if (insertchar(&p, s[i]))
                {
                    allfree(&p, s);
                    return -1;
                }
            }
            else if (s[i] == '\\')
                isslash = 1;
            else if (s[i] == '$')
            {
                isparam = 1;
                param = initstr();
                if (param == NULL)
                {
                    allfree(&p, s);
                    return -1;
                }
            }
            else if (is2quot)
            {
                if (s[i] == '"')
                    is2quot = 0;
                else if (insertchar(&p, s[i]))
                {
                    allfree(&p, s);
                    return -1;
                }
            }
            else if (s[i] == '\'')
                isquot = 1;
            else if (s[i] == '"')
                is2quot = 1;
            else if (s[i] == '#')
                isignore = 1;
            else if (s[i] == '<' || s[i] == '>' || s[i] == '|' || s[i] == ';' || s[i] == '&'
                    || s[i] == ' ' || s[i] == '\t')
            {
                if (s[i] == '\t')
                    s[i] = ' ';
                if (insertspec(&p, s[i]))
                {
                    allfree(&p, s);
                    return -1;
                }
            }
            else
            {
                if (insertchar(&p, s[i]))
                {
                    allfree(&p, s);
                    return -1;
                }
            }
        }
        finished = 1;
        if (isquot || (is2quot && !isslash))
        {
            if (insertchar(&p, '\n'))
            {
                allfree(&p, s);
                return -1;
            }
            finished = 0;
        }
        else if (isbrack && !isslash)
        {
            param = push_back(param, '\n');
            if (param == NULL)
            {
                allfree(&p, s);
                return -1;
            }
            finished = 0;
        }
        else if (isslash)
        {
            isslash = 0;
            finished = 0;
        }
    }
    if (isparam)
    {

        char *rparam = getparam(param);
        if (rparam == NULL)
        {
            free(param);
            allfree(&p, s);
            return -1;
        }
        for (j = 0; j < strlen(rparam); ++j)
        {
            if (insertchar(&p, rparam[j]))
            {
                allfree(&p, s);
                return -1;
            }
        }
        free(param);
        isparam = 0;
    }
    if (isslash || isquot || is2quot)
    {
        seterr(ERR_PARSE_END);
        allfree(&p, s);
        return -1;
    }
    if (p.state == 4 && p.cur->redin == NULL)
    {
        seterr(ERR_PARSE_REDIN);
        allfree(&p, s);
        return -1;
    }
    else if (p.state == 5 && p.cur->redout == NULL)
    {
        seterr(ERR_PARSE_REDOUT);
        allfree(&p, s);
        return -1;
    }
    (*res) = p.j;
    if (s != NULL)
        free(s);
    return p.k;
}

