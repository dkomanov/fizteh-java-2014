#ifndef MRUN
#define MRUN
#include <termios.h>
#include <sys/types.h>

struct runj
{
    char *c;
    int k;
    pid_t *p;
    int *status;
    int *state;
    char rep;
    struct termios t;
};

extern int exflag;
extern void free_runner();
extern void update_status();
extern struct runj* rjobs;
extern int jobcnt;
extern void launch_process(struct job*, pid_t, int, int, int, char);
extern void launch_job(struct job *j);

#endif
