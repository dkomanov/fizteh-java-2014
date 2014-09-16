#define _POSIX_C_SOURCE 200809L

#include <stdio.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <unistd.h>
#include <stdlib.h>
#include "merr.h"
#include "mgets.h"
#include <signal.h>
#include "param.h"
#include <errno.h>
#include "parser.h"
#include "runner.h"
#include <sys/types.h>
#include <termios.h>
#include "term_helper.h"
#include <string.h>
#include <sys/wait.h>
#include "mbuilds.h"

extern char* get_current_dir_name();
struct runj* rjobs = NULL;
int jobcnt = 0;
int exflag = 0;

void free_runner()
{
    int i;
    for (i = 0; i < jobcnt; ++i)
    {
        free(rjobs[i].c);
        free(rjobs[i].p);
        free(rjobs[i].status);
        free(rjobs[i].state);
    }
    free(rjobs);
    jobcnt = 0;
}

void launch_process(struct job* j, pid_t pgid, int infile,
        int outfile, int errfile, char foreground)
{
    
    pid_t pid;
    if (is_interactive)
    {
        pid = getpid();
        if (pgid == 0) pgid = pid;
        setpgid(pid, pgid);
        if (foreground)
            tcsetpgrp(term, pgid);
        signal(SIGINT, SIG_DFL);
        signal(SIGQUIT, SIG_DFL);
        signal(SIGTSTP, SIG_DFL);
        signal(SIGTTIN, SIG_DFL);
        signal(SIGTTOU, SIG_DFL);
        signal(SIGCHLD, SIG_DFL);
    }
    if (infile != STDIN_FILENO)
    {
        dup2(infile, STDIN_FILENO);
        close(infile);
    }
    if (outfile != STDOUT_FILENO)
    {
        dup2(outfile, STDOUT_FILENO);
        close(outfile);
    }
    if (errfile != STDERR_FILENO)
    {
        dup2(errfile, STDERR_FILENO);
        close(errfile);
    }
    j->args = realloc(j->args, (j->argv + 1) * sizeof(char*));
    j->args[j->argv] = NULL;
    run2_bi(j);
    execvp(j->args[0], j->args);
    printf("Program not found\n");
    exit(0);
}

void put_background(int ij, char cont)
{
    if (cont)
    {
        printf("\x1B[32m[%d]  %d - %s continued\x1B[0m\n", ij, rjobs[ij].p[0], rjobs[ij].c);
        if (kill(-rjobs[ij].p[0], SIGCONT) < 0)
        {
            printf("Error with sending kill\n");
        }
    }
    else
    {
        printf("\x1B[32m[%d]  %d - %s started\x1B[0m\n", ij, rjobs[ij].p[0], rjobs[ij].c);
    }
}

int add_job(char *c)
{
    int ji = jobcnt;
    jobcnt++;
    rjobs = realloc(rjobs, jobcnt * sizeof(struct runj));
    if (rjobs == NULL)
    {
        printf("realloc error\n");
        exit(0);
    }
    rjobs[ji].k = 0;
    rjobs[ji].c = malloc(strlen(c) + 1);
    memcpy(rjobs[ji].c, c, strlen(c) + 1);
    rjobs[ji].p = malloc(1);
    rjobs[ji].state = malloc(1);
    rjobs[ji].status = malloc(1);
    rjobs[ji].rep = 1;
    tcgetattr(term, &rjobs[ji].t);
    return 0;
}

int add_proc(struct job *j, pid_t pid, int ji)
{
    int mk = rjobs[ji].k;
    rjobs[ji].k++;
    rjobs[ji].state = realloc(rjobs[ji].state, sizeof(int) * rjobs[ji].k);
    rjobs[ji].p = realloc(rjobs[ji].p, sizeof(pid_t) * rjobs[ji].k);
    rjobs[ji].status = realloc(rjobs[ji].status, sizeof(int) * rjobs[ji].k);
    rjobs[ji].p[mk] = pid;
    rjobs[ji].status[mk] = 0;
    rjobs[ji].state[mk] = 0;
    return 0;
}

int mark_proc_state(pid_t pid, int status)
{
    int i, j;
    if (pid > 0)
    {
        for (i = 0; i < jobcnt; ++i)
            for (j = 0; j < rjobs[i].k; ++j)
                if (rjobs[i].p[j] == pid)
                {
                    if (WIFSTOPPED(status))
                        rjobs[i].state[j] = 1;
                    else
                    {
                        rjobs[i].state[j] = 2;
                        if (WIFSIGNALED(status))
                            printf("\x1B[33mChild  [%d]   %d - %s terminated by signal %d, exit status - %d\x1B[0m\n", i, (int)pid, rjobs[i].c, WTERMSIG(status), WEXITSTATUS(status));
                        else if (WEXITSTATUS(status))
                            printf("\x1B[33mChild  [%d]   %d - %s returns exit status - %d\x1B[0m\n", i, (int)pid, rjobs[i].c, WEXITSTATUS(status));
                        
                    }
                    rjobs[i].rep = 1;
                    rjobs[i].status[j] = status;
                    return 0;
                }
        printf("No child with pid %d\n", pid);
        return 1;
    }
    else if (pid == 0 || errno == ECHILD)
        return 1;
    else
    {
        printf("pid %d !> 0\n", pid);
        exit(0);
    }
}

void check_childs()
{
    int status;
    pid_t pid;

    do
    {
        pid = waitpid(-1, &status, WUNTRACED|WNOHANG);
    }
    while (!mark_proc_state(pid, status));
}

char job_is_s(int ji)
{
    int i;
    for (i = 0; i < rjobs[ji].k; ++i)
        if (rjobs[ji].state[i] != 1)
            return 0;
    return 1;
}

char job_is_f(int ji)
{
    int i;
    for (i = 0; i < rjobs[ji].k; ++i)
        if (rjobs[ji].state[i] != 2)
            return 0;
    return 1;
}

char job_is_sf(int ji)
{
    return job_is_s(ji) || job_is_f(ji);
}

void update_status()
{
    int i;
    check_childs();
    for (i = 0; i < jobcnt; ++i)
        if (rjobs[i].rep)
        {
            rjobs[i].rep = 0;
            if (job_is_s(i))
                printf("\x1B[32m[%d]  %d - %s suspended \x1B[0m\n", i, rjobs[i].p[0], rjobs[i].c);
            else if (job_is_f(i))
                printf("\x1B[32m[%d]  %d - %s stopped \x1B[0m\n", i, rjobs[i].p[0], rjobs[i].c);
        }
}


void wait_for_job(int i)
{
    int status;
    pid_t pid;

    do
    {
        pid = waitpid(-1, &status, WUNTRACED);
    }
    while (!mark_proc_state(pid, status) && (!job_is_sf(i)));
}

void put_foreground(int ji, int cont)
{
    tcsetpgrp(term, rjobs[ji].p[0]);
    if (cont)
    {
        tcsetattr(term, TCSADRAIN , &rjobs[ji].t);
        if (kill(-rjobs[ji].p[0], SIGCONT) < 0)
        {
            printf("Can't run kill command\n");
            exit(0);
        }
    }
    wait_for_job(ji);
    rjobs[ji].rep = 0;
    if (rjobs[ji].state[0] == 1)
        printf("\x1B[32m[%d]  %d - %s suspended\x1B[0m\n", ji, rjobs[ji].p[0], rjobs[ji].c);
    tcsetpgrp(term, shell_pgid);
    tcgetattr(term, &rjobs[ji].t);
    tcsetattr(term, TCSADRAIN, &sh_mode);
}

void print_jobs()
{
    int i = 0;
    for (i = 0; i < jobcnt; ++i)
        if (!job_is_f(i))
        {
            if (job_is_s(i))
                printf("[%d]  %d - %s is suspended \n", i, rjobs[i].p[0], rjobs[i].c);
            else
                printf("[%d]  %d - %s is running \n", i, rjobs[i].p[0], rjobs[i].c);
        }
}

void mark_as_runned(int ji)
{
    int i;
    for (i = 0; i < rjobs[ji].k; ++i)
        rjobs[ji].status[i] = 0;
}

void continue_job(int ji, char foreground)
{
    mark_as_runned(ji);
    if (foreground)
        put_foreground(ji, 1);
    else
        put_background(ji, 1);
}

int getid(struct job *j)
{
    int i, n;
    if (j->argv == 1)
    {
        if (jobcnt == 0)
            printf("No last job\n");
        return jobcnt - 1;
    }
    else if (j->argv > 2)
    {
        printf("Too many arguments\n");
        return -1;
    }
    else
    {
        n = strlen(j->args[1]);
        for (i = 0; i < n; ++i)
            if (j->args[1][i] < '0' || j->args[1][i] > '9')
            {
                printf("Non numerical char in args\n");
                return -1;
            }
        if (n > 9)
        {
            printf("Too long number\n");
            return -1;
        }
        n = atoi(j->args[1]);
        if (n >= jobcnt)
        {
            printf("No job with this number\n");
            return -1;
        }
        return n;
    }
}

void bi_jobs(struct job *j)
{
    print_jobs();
}

void bi_fg(struct job *j)
{
   int k = getid(j);
   if (k >= 0 && !job_is_f(k))
       continue_job(k, 1);
}

void bi_bg(struct job *j)
{
    int k = getid(j);
    if (k >= 0 && !job_is_f(k))
        continue_job(k, 0);
}

void bi_exit(struct job *j)
{
    exflag = 1;
}

void bi_export(struct job *j)
{
    if (j->argv == 4 && !strcmp("=", j->args[2]))
        setenv(j->args[1], j->args[3], 1);
    else
        printf("Please run *NAME* = *VALUE* without spaces around =.\n");
}

void bi_cd(struct job *j)
{
    int k;
    char *q;
    if (j->argv == 1)
        k = chdir(getparam("HOME"));
    else
        k = chdir(j->args[1]);
    if (k != 0)
        printf("Error: no such file or directory\n");
    else
    {
        q = (char*)get_current_dir_name();
        setenv("PWD", q, 1);
        free(q);
    }
}

int try_build_in(struct job *j)
{
    if (j->next != NULL)
        return 0;
    if (!strcmp(j->args[0], "fg"))
    {
        bi_fg(j);
        return 1;
    }
    if (!strcmp(j->args[0], "bg"))
    {
        bi_bg(j);
        return 1;
    }
    if (!strcmp(j->args[0], "exit"))
    {
        bi_exit(j);
        return 1;
    }
    if (!strcmp(j->args[0], "export"))
    {
        bi_export(j);
        return 1;
    }
    if (!strcmp(j->args[0], "cd"))
    {
        bi_cd(j);
        return 1;
    }
    if (!strcmp(j->args[0], "jobs"))
    {
        bi_jobs(j);
        return 1;
    }
    return 0;
}

void launch_job(struct job *j)
{
    pid_t pid;
    int ji = jobcnt;
    char bg = j->bg == 1;
    pid_t mpid = 0;
    int mpipe[2];
    int infile, outfile;
    infile = STDIN_FILENO;

    if (try_build_in(j))
        return;
    add_job(j->args[0]);
    if (j->redin)
        infile = open(j->redin, O_RDONLY);
    for (; j; j = j->next)
    {
        if (j->next)
        {
            if (pipe(mpipe) < 0)
            {
                printf("Can't create pipe\n");
                exit(0);
            }
            outfile = mpipe[1];
        }
        else if (j->redout)
        {
            if (j->out_append)
                outfile = open(j->redout, O_WRONLY | O_CREAT | O_APPEND, 0644);
            else
                outfile = open(j->redout, O_WRONLY | O_CREAT, 0644);
        }
        else
            outfile = STDOUT_FILENO;
        pid = fork();
        if (pid == 0)
            launch_process(j, mpid, infile, outfile, STDERR_FILENO, !bg);
        else if (pid < 0)
        {
            printf("fork error\n");
            exit(0);
        }
        else
        {
            if (mpid == 0)
                mpid = pid;
            if (add_proc(j, pid, ji))
            {
                printf("add proc error\n");
                exit(0);
            }
            if (is_interactive)
            {
                setpgid(pid, mpid);
            }

        }
        if (infile != STDIN_FILENO)
            close(infile);
        if (outfile != STDOUT_FILENO)
            close(outfile);
        infile = mpipe[0];
    }
    if (!is_interactive)
        wait_for_job(ji);
    else if (bg)
        put_background(ji, 0);
    else
        put_foreground(ji, 0);
}

