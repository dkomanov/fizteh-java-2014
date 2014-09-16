#include "merr.h"
#include <stdlib.h>

int merrno = 0;

char* mess[] = {"No error", "Memory error", "Variable not found", "Some parse error",
    "Error with redirection in",
    "Error with redirection out", "Parse error near &", "Parse error near ;", "Parse error near |",
    "Parse error in the end of the line", "Parse error end of file"};

void seterr(int n)
{
    merrno = n;
}

char *get_message(int n)
{
    if (n < 10)
        return mess[n];
    return NULL;
}

int geterr()
{
    return merrno;
}
