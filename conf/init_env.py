#!/usr/bin/env python
import sys, os

def echo_exe(cmd):
    print cmd
    os.system(cmd)

def create_links():
    conf_path = os.path.abspath('.')
    if not os.path.exists(conf_path):
        print 'Please execute in fklab dir.'
        sys.exit(1)
    echo_exe('ln -sf %s/bashrc ~/.bashrc' % conf_path)
    echo_exe('ln -sf %s/_emacs ~/.emacs' % conf_path)
    echo_exe('ln -sf %s/vimrc ~/.vimrc' % conf_path)
    echo_exe('ln -sf %s/vim ~/.vim' % conf_path)
    echo_exe('ln -sf %s/bashrc.root /root/.bashrc' % conf_path)
    echo_exe('ln -sf %s/vimrc /root/.vimrc' % conf_path)
    echo_exe('ln -sf %s/vim /root/.vim' % conf_path)
    sys.stdout.flush()

def check_links():
    os.system('echo Checking ...')
    os.system('ls -l --color=auto ~/.bashrc')
    os.system('ls -l --color=auto ~/.emacs')
    os.system('ls -l --color=auto ~/.vimrc')
    os.system('ls -l --color=auto ~/.vim')
    os.system('ls -l --color=auto /root/.bashrc')
    os.system('ls -l --color=auto /root/.vimrc')
    os.system('ls -l --color=auto /root/.vim')


if __name__ == '__main__':
    if not os.getuid() == 0:
        print 'Require root privilege.'
        sys.exit(1)
    print 'Initializing environment ...'
    create_links()
    check_links()
    print 'Initialized environment.'



