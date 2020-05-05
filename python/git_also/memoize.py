import functools


# cache function - func
def memoize(func):
    cache = func.cache = {}

    @functools.wraps(func)
    def memoized_func(*args):
        key = str(args)
        if key not in cache:
            cache[key] = func(*args)
        return cache[key]

    return memoized_func
