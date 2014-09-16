#ifndef MERR
#define MERR

extern char* get_message(int n);
extern void seterr(int);
extern int geterr();
#ifndef _NO_ERR_DEFINES

#define ERR_MEM 1
#define ERR_PARAM_NOT_FOUND 2
#define ERR_PARSE 3
#define ERR_PARSE_REDIN 4
#define ERR_PARSE_REDOUT 5
#define ERR_PARSE_BG 6
#define ERR_PARSE_FG 7
#define ERR_PARSE_CONV 8
#define ERR_PARSE_END 9
#define ERR_PARSE_END_FILE 10
#endif


#endif
