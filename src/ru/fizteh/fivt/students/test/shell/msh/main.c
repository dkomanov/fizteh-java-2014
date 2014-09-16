#include <stdio.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <unistd.h>
#include <stdlib.h>
#include "merr.h"
#include "mgets.h"
#include <signal.h>
#include "param.h"
#include "parser.h"
#include "runner.h"
#include "term_helper.h"
#include <sys/types.h>
#include <termios.h>

void debug_oupt(struct job* j, int k)
{
    int i, q;
    struct job *cur;
    printf("Find %d jobs:\n\n", k);
    for (i = 0; i < k; ++i)
    {
        printf("Job # %d:\n", i);
        cur = j + i;
        while (cur != NULL)
        {
            printf("\t%d args\n", cur->argv);
            for (q = 0; q < cur->argv; ++q)
                printf("\t arg # %d - %s\n", q, cur->args[q]);
            if (cur->redin != NULL)
                printf("\tinput redirect - %s\n", cur->redin);
            if (cur->out_append)
                printf("\toutput append\n");
            if (cur->redout != NULL)
                printf("\toutput redirect - %s\n", cur->redout);
            if (cur->bg == 0)
                printf("\t none \n");
            else if (cur->bg == 1)
                printf("\tbg &\n");
            else if (cur->bg == 2)
                printf("\tbg ;\n");
            else if (cur->bg == 3)
                printf("\tbg ||\n");
            else if (cur->bg == 4)
                printf("\tbg &&\n");
            printf("\n");
            cur = cur->next;
        }
        printf("END JOB\n\n\n");
    }
}
#ifdef RUN
int main()
{
    struct job* res;
    int k = 0;
    int i;
    init_shell();
    while (k != -2 && !exflag)
    {
        k = parse(0, &res);
        for (i = 0; i < k; ++i)
        {
            update_status();
            launch_job(res + i);
        }
        update_status();
        if (k >= 0)
            parsefree(res, k);
        else if (k == -1)
            printf("\n ERROR # %d - %s\n", geterr(), get_message(geterr()));
    }
    free_param();
    free_runner();
    return 0;
}
#else
int main()
{
    struct job* res;
    int k = 0;
    while (k != -2)
    {
        k = parse(0, &res);
        debug_oupt(res, k);
        if (k >= 0)
            parsefree(res, k);
        else if (k == -1)
            printf("\n ERROR # %d - %s\n", geterr(), get_message(geterr()));
        printf("%d\n", k);
    }
    return 0;
}
#endif
