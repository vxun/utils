#!/usr/bin/env python

#################################################################
# 在做Hadoop大数据集群时，
# 清空生成的namenode和datanode数据脚本
#################################################################
import os,sys
import shutil
dir = sys.argv[1]
print(dir)
if dir == "data":
    shutil.rmtree("/space1/hdfsdata/")
    os.mkdir("/space1/hdfsdata/")
    shutil.rmtree("/space2/hdfsdata/")
    os.mkdir("/space2/hdfsdata/")
    shutil.rmtree("/space3/hdfsdata/")
    os.mkdir("/space3/hdfsdata/")
    shutil.rmtree("/space3/hdfsdata/")
    os.mkdir("/space3/hdfsdata/")
    shutil.rmtree("/space4/hdfsdata/")
    os.mkdir("/space4/hdfsdata/")
if dir == "name":
    if os.path.exists("/space1/hdfsname/"):
        shutil.rmtree("/space1/hdfsname/")
        os.mkdir("/space1/hdfsname/")
    if os.path.exists("/space2/hdfsname/"):
        shutil.rmtree("/space2/hdfsname/")
        os.mkdir("/space2/hdfsname/")
    if os.path.exists("/space3/hdfsname/"):
        shutil.rmtree("/space3/hdfsname/")
        os.mkdir("/space3/hdfsname/")
    if os.path.exists("/space4/hdfsname/"):
        shutil.rmtree("/space4/hdfsname/")
        os.mkdir("/space4/hdfsname/")
print("end")
