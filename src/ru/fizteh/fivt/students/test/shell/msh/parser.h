#ifndef MPARSER
#define MPARSER

struct job
{
    char **args;
    int argv;
    char *redin;
    char *redout;
    struct job *next;
    /*
     * 0 - none
     * 1 - &
     * 2 - ;
     * 3 - ||
     * 4 - &&
     */
    char bg;
    char out_append;
};

struct job_parse
{
    struct job* j;
    int k;
    char was_space;
    struct job* cur;
    char state;
    char ischar;
};

extern void parsefree(struct job *x, int k);
extern int parse(int file, struct job** res);

#endif
