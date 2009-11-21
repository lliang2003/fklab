#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 26 06:35:16 CST 2008

import sys, readline, atexit, os

def save_history(histfile):
    readline.write_history_file(histfile)

class MyCompleter:
    def __init__(self):
        self.words = "perl", "pyjamas", "python", "pythagoras"
        self.prefix = None
    def complete(self, prefix, index):
        print prefix, index
        if prefix != self.prefix:
            # we have a new prefix!
            # find all words that start with this prefix
            self.matching_words = [
                w for w in self.words if w.startswith(prefix)
                ]
            self.prefix = prefix
        try:
            return self.matching_words[index]
        except IndexError:
            return None

def test_readline():
    print '-'*48, '\nFunc:', sys._getframe().f_code.co_name
    words = "perl", "pyjamas", "python", "pythagoras"
    histfile = os.path.expanduser('~/.bash_history')
    readline.read_history_file(histfile)
    atexit.register(readline.write_history_file, histfile)
    readline.parse_and_bind("tab: complete")
    readline.set_completer(MyCompleter().complete)
    for i in range(3):
        print(raw_input('$'))


test_readline()
