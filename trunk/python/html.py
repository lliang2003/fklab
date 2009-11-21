#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Apr 22 21:07:32 CST 2009

import re, sys

def html2text(page):
    page = re.sub('<script[^<]*</script>', '', page)
    page_text = re.sub('<[^>]*>|&[^;]*;', '', page)
    return page_text


