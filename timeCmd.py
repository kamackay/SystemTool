import subprocess
import sys
from datetime import datetime


def log(*args, **kwargs):
    sys.stdout.write(*args)
    sys.stdout.flush()
    if kwargs.pop("toFile", False):
        with open("timeCmd.log", "a+") as f:
            f.write(*args)


if __name__ == "__main__":
    def quotes(text): return text if ' ' not in text else f'"{text}"'
    arguments = sys.argv
    cmd = ""
    for x in range(1, len(arguments)):
        if x >= 2:
            cmd += quotes(arguments[x]) + " "
        else:
            # Don't want to put quotes around the first thing
            cmd += arguments[x] + " "
    log(f"\nExecuting Command : {cmd}\n")
    startTime = datetime.now()
    popen = subprocess.Popen(cmd, stdout=subprocess.PIPE,
                             stderr=subprocess.PIPE, universal_newlines=True)
    for stdout_line in iter(popen.stdout.readline, ""):
        log("\t" + stdout_line)
    popen.stdout.close()
    return_code = popen.wait()
    endTime = datetime.now()
    log(f"\nTook {(endTime - startTime).total_seconds()} seconds to execute")
