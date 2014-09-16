#include "merr.h"
#include <stdlib.h>
#include <unistd.h>
#include "mgets.h"
#include <stdio.h>

extern int getpagesize();

char* mgets(int f)
{
    char* s = (char*)malloc(getpagesize());
    char* q;
    ssize_t eof;
    size_t l, i;
    if (s == NULL)
    {
        seterr(ERR_MEM);
        return NULL;
    }
    l = 1;
    i = 0;
    eof = read(f, s, 1);
    if (eof == 0)
    {
        free(s);
        return NULL;
    }
    while (s[i] != '\n' && eof && s[i] != EOF)
    {
        i++;
        while (i >= l)
        {
            l += getpagesize();
            q = (char*)realloc(s, l);
            if (q == NULL)
            {
                seterr(ERR_MEM);
                free(s);
                return NULL;
            }
            s = q;  
        }
        eof = read(f, s + i, 1);
    }
    s[i] = 0;
    q = (char*)realloc(s, i + 1);
    if (q == NULL)
    {
        seterr(ERR_MEM);
        free(s);
        return NULL;
    }
    return q;
}
