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
#include "parser.h"
#include "runner.h"
#include <sys/types.h>
#include <termios.h>


char is_interactive = 0;
struct termios sh_mode;
pid_t shell_pgid;
int term = STDIN_FILENO;


void init_shell()
{

    if (param_init())
    {
        write(term, "Params init failed.", 19);
    }
    is_interactive = isatty(term);
    if (is_interactive)
    {
        while (tcgetpgrp(term) != (shell_pgid = getpgrp()))
            kill(-shell_pgid, SIGTTIN);
        signal(SIGINT, SIG_IGN);
        signal(SIGQUIT, SIG_IGN);
        signal(SIGTSTP, SIG_IGN);
        /*signal(SIGTTIN, SIG_IGN);*/
        signal(SIGTTOU, SIG_IGN);
        /*signal(SIGCHLD, SIG_IGN);*/
        shell_pgid = getpid();
        if (setpgid(shell_pgid, shell_pgid) < 0)
        {
            write(term, "Shell is on on foreground!", 26);
            exit(0);
        }
        tcsetpgrp(term, shell_pgid);
        tcgetattr(term, &sh_mode);
    }
}
