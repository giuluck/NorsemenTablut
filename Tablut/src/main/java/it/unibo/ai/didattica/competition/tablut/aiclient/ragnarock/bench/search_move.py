import sys, os, timeit, cProfile, pstats
sys.path.append(os.path.dirname(os.path.realpath(__file__)) + "/../src")

def main():
    import multiprocessing as mp
    mp.set_start_method('fork')
    setup = "\
from random import randrange \n\
import numpy as np \n\
from copy import deepcopy \n\
from search import Search \n\
search = Search(1, float('infinity')) \n\
state_obj = [ \n\
    [ 0,  0,  0, -1, -1, -1,  0,  0,  0], \n\
    [ 0,  0,  0,  0, -1,  0,  0,  0,  0], \n\
    [ 0,  0,  0,  0,  1,  0,  0,  0,  0], \n\
    [-1,  0,  0,  0,  1,  0,  0,  0, -1], \n\
    [-1, -1 , 1 , 1 , 2,  1,  1, -1, -1], \n\
    [-1,  0,  0,  0,  1,  0,  0,  0, -1], \n\
    [ 0,  0,  0,  0,  1,  0,  0,  0,  0], \n\
    [ 0,  0,  0,  0, -1,  0,  0,  0,  0], \n\
    [ 0,  0,  0, -1, -1, -1,  0,  0,  0]] \n"

    search_obj = "search.start(state_obj)"

    '''
    print("====== Benchmark alphabeta search python objects =====")
    print(avg_time(search_obj, setup, 1000))
    '''

    print("====== Profiler alphabeta search numpy =====")
    cProfile.run(setup+search_obj, sort="tottime", filename="/tmp/bench_alphabeta_search_obj.txt")
    p = pstats.Stats("/tmp/bench_alphabeta_search_obj.txt")
    p.sort_stats("tottime").print_stats(30)
    p.sort_stats("ncalls").print_stats(30)

    print("====== All benchmark completed! =====")

def avg_time(stm, setup, n):
    return str(timeit.timeit(stm, setup=setup, number=n) / n * 1e6) + " us"

if __name__ == '__main__': main()