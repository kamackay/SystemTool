import os
import sys
import re

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

    print(f"\n\nLargest File: {biggest[0]} bytes - \"{biggest[1]}\"")
