#ifndef MTERM
#define MTERM

#include <sys/types.h>

extern char is_interactive;
extern struct termios sh_mode;
extern pid_t shell_pgid;
extern int term;


extern void init_shell();


#endif
