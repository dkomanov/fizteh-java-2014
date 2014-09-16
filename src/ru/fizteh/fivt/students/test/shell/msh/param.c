#define _POSIX_C_SOURCE 200809L

#include "param.h"
#include "merr.h"
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <sys/stat.h>

extern pid_t getpgid(pid_t);
extern int gethostname(char *name, size_t len);
extern ssize_t readlink(const char *path, char *buf, size_t bufsiz);

char*** a;
int cnt = 0;

char *get_bi(char *s)
{
    int i;
    for (i = 0; i < cnt; ++i)
        if (!strcmp(s, a[i][0]))
            return a[i][1];
    return NULL;
}

void free_param()
{
    int i;
    for (i = 0; i < cnt; ++i)
    {
        free(a[i][1]);
        free(a[i]);
    }
    cnt = 0;
    free(a);
}

int param_init()
{
    char *buffer;
    int i;
    cnt = 3;
    a = malloc(cnt * sizeof(char**));
    if (a == NULL)
        return 1;
    for (i = 0; i < cnt; ++i)
    {
        a[i] = malloc(2 * sizeof(char*));
        if (a[i] == NULL)
            return -1;
    }
    buffer = (char*)malloc(10 * sizeof(char));
    if (buffer == NULL)
        return 1;
    sprintf(buffer, "%d", getuid());
    a[0][0] = "UID";
    a[0][1] = buffer;

    buffer = (char*)malloc(10 * sizeof(char));
    if (buffer == NULL) exit(3);
    sprintf(buffer, "%d", getpid());
    a[1][0] = "PID";
    a[1][1] = buffer;

    buffer = (char*)malloc(40 * sizeof(char));
    if (buffer == NULL) exit(3);
    gethostname(buffer, 39);
    a[2][0] = "HOSTNAME";
    a[2][1] = buffer;
    
    buffer = (char*)malloc(200 * sizeof(char));
    if (buffer == NULL) exit(3);
    i = readlink("/proc/self/exe", buffer, sizeof(char) * 200);
    buffer[i] = 0;
    setenv("SHELL", buffer, 1);
    free(buffer);
    return 0; 
}

char* getparam(char *s)
{
    char *q = get_bi(s);
    if (q == NULL)
        q = getenv(s);
    if (q == NULL)
        q = "";
    return q;
}
