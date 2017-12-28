from urllib import request
import time
import datetime
import sys


def log(s: str = ""):
    line = f"{datetime.datetime.now().strftime('%Y/%m/%d %H:%M:%S - ')} {s}"
    print(line)
    with open("c:\\internet.log", "a") as f:
        f.write(line + "\n")


def internet_on(timeout: int = 1):
    try:
        request.urlopen('http://216.58.192.142', timeout=timeout)
        return True
    except urllib2.URLError as err:
        return False


if __name__ == "__main__":
    verbose = "--v" in sys.argv
    log("Now checking internet status")
    try:
        while True:
            if not internet_on():
                log("No Internet!")
                while not internet_on(timeout=10):
                    time.sleep(1)  # wait for the Internet to come back
            elif verbose:
                log("Internet is fine")
            time.sleep(10)
    except:
        log()
        log("Exiting")
