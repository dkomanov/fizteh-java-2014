#include "mgets.h"
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include "mbuilds.h"
#include "parser.h"



int mcat(struct job *j)
{
    char *q;
    while (NULL != (q = mgets(0)))
    {
        printf("%s\n", q);
        free(q);
    }
    return 0;
}

int mcmp(const void *a, const void *b)
{
    return strcmp(*(char**)a, *(char**)b);
}


int inmsed(int fin, int fout, char* pattern, char* res)
{
    int k;
    int n = strlen(pattern);
    int nr = strlen(res);
    char *q = malloc(n);
    if (q == NULL)
        return -1;
    while (n == (k = read(fin, q, n)))
    {
        while (memcmp(pattern, q, n))
        {
            write(fout, q, 1);
            memmove(q, q + 1, n - 1);
            if (!read(fin, q + n - 1, 1))
            {
                write(fout, q, n - 1);
                free(q);
                return 0;
            }
        }
        write(fout, res, nr);
    }
    write(fout, q, k);
    free(q);
    return 0;
}


int msort(struct job *j)
{
    char *q, **w;
    int i;
    int cnt = 0;
    char **s = NULL;
    if (j->argv != 1)
    {
        printf("Usage msort < infile > outfile\n");
        return 1;
    }
    while (NULL != (q = mgets(0)))
    {
        cnt++;
        w = realloc(s, sizeof(char*) * cnt);
        if (w == NULL)
            exit(1);
        s = w;
        s[cnt - 1] = q;
    }
    qsort(s, cnt, sizeof(char*), mcmp);
    for (i = 0; i < cnt; ++i)
    {
        printf("%s\n", s[i]);
        free(s[i]);
    }
    free(s);
    return 0;
}

int msed(struct job *mj)
{
    if (mj->argv == 3)
        inmsed(0, 1, mj->args[1], mj->args[2]);
    else
        printf("usage: msed 'pattern' 'res' < infile > outfile\n");
    return 0;
}

int run2_bi(struct job *j)
{
    if (!strcmp(j->args[0], "msed"))
        exit(msed(j));
    if (!strcmp(j->args[0], "mcat"))
        exit(mcat(j));
    if (!strcmp(j->args[0], "msort"))
        exit(msort(j));
    return 0;
}
