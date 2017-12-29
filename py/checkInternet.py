from urllib import request
import time
from datetime import datetime
import sys


def log(s: str = ""):
    line = f"{datetime.now().strftime('%Y/%m/%d %H:%M:%S - ')} {s}"
    print(line)
    with open("c:\\keith\\drive\\internet\\internet.log", "a") as f:
        f.write(line + "\n")


def internet_on(timeout: int = 1):
    try:
        request.urlopen('http://216.58.192.142', timeout=timeout)
        return True
    except Exception as err:
        return False


if __name__ == "__main__":
    verbose = "--v" in sys.argv
    log("Now checking internet status")
    outages = 0
    try:
        while True:
            if not internet_on():
                log("Connection Fail - Potential outage")
                out_time = datetime.now()
                while not internet_on(timeout=1):
                    pass
                time_out = (datetime.now() - out_time).total_seconds()
                if time_out >= 1:
                    outages += 1
                    log(f"\t\tInternet outage for {time_out} seconds - #{outages}")
                else:
                    log("\t\tMinor outage")
            elif verbose:
                log("Internet is fine")
            time.sleep(10)
    except Exception as e:
        log(f"An exception of type {type(ex).__name__} occurred. Arguments:\n'{ex.args}'")
        log("Exiting")
