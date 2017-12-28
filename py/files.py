import os
import sys
import re


def bytes_to_readable(byte_count: int) -> str:
    unit = {
        0: "B ",
        1: "KB",
        2: "MB",
        3: "GB",
        4: "TB",
        5: "PB"
    }
    x = 0
    while byte_count > 1024:
        x += 1
        byte_count /= 1024
    return f"{byte_count} {unit[x]}"


if __name__ == "__main__":

    biggest = ("", -1)

    def search(dir, match):
        global biggest
        for item in os.listdir(dir):
            item = os.path.join(dir, item)
            if os.path.isdir(item):
                search(item, match)
            elif match.match(item):
                itemsize = os.path.getsize(item)
                if itemsize > biggest[1]:
                    biggest = (item, itemsize)
                    # print(f"New Largest file: {itemsize} bytes - \"{item}\"")

    search(sys.argv[1] if len(sys.argv) > 1 else "C:\\projects\\opentms",
           sys.argv[2] if len(sys.argv) > 2 else re.compile(".*"))

    print(f"\n\nLargest File: {biggest[0]} - {bytes_to_readable(biggest[1])}")
