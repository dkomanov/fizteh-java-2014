CC=gcc
CFLAGS= -fdiagnostics-color=always  -ansi -pedantic -Wall -g 

runner: runner.c term_helper.c main.c merr.c mgets.c parser.c param.c mbuilds.c
	$(CC) $(CFLAGS) -g -DRUN -o main $^

parser: runner.c term_helper.c main.c merr.c mgets.c parser.c param.c mbuilds.c
	$(CC) $(CFLAGS) -o main $^


clean:
	rm -r -f main

.PHONY: clean
