#!/usr/bin/env python

import math
import random

nRounds = 10000
nInputs = 3
nHidden = 8
nPatterns = 4

LR_IH = 0.7
LR_HO = 0.07

trainInput = []
trainOutput = []
weightIH = []
weightHO = []
valHidden = []
output = 0
diff = 0

def calcNN(pattern):
    output = 0.0
    for h in range(0, nHidden):
        x = 0
        for i in range(0, nInputs):
            x += trainInput[pattern][i] * weightIH[i][h]
#            print trainInput[pattern][i], weightIH[i][h]
#            print x, trainInput[pattern][i], weightIH[i][h]
        valHidden[h] = 1.0 / (1 + math.exp(-x))
#        print h, valHidden[h]
#        print "Value Hidden ",h,"=", valHidden[h]
        output += valHidden[h] * weightHO[h]
#    print "Output:", output, "Expect:", trainOutput[pattern]
    diff = output * (1 - output) * (trainOutput[pattern] - output)
    return output - trainOutput[pattern]
    
def changeWeightHO(error):
    for h in range(0, nHidden):
        weightHO[h] -= LR_HO * error * valHidden[h]
#        weightHO[h] -= LR_HO * error * (1 - output) * output * valHidden[h]
#        print "weight",h,"=",weightHO[h],"error=", error
        if weightHO[h] > 5: weightHO[h] = 5
        if weightHO[h] < -5: weightHO[h] = -5
#        print "weight",h,"=",weightHO[h],"error=", error
        
def changeWeightIH(pattern, error):
    for h in range(0, nHidden):
#        change = valHidden[h] * (1 - valHidden[h])
#        change *= LR_IH * weightHO[h] * error
        change = 1 - (valHidden[h] ** 2)
        change *= LR_IH * error * weightHO[h]
        for i in range(0, nInputs):
#            print weightIH[i][h]
            weightIH[i][h] -= change * trainInput[pattern][i]
#            print "weight",i,h,"=",weightIH[i][h],"change=", change, error, trainInput[pattern][i]
#            if weightIH[i][h] > 10: weightIH[i][h] = 10
#            if weightIH[i][h] < -10: weightIH[i][h] = -10
                
                        
def initWeights():
    for h in range(0, nHidden):
        weightHO[h] = 1.5 - h
        for i in range(0, nInputs):
            weightIH[i][h] = 1 - i
def initData():
    for h in range(0, nHidden):
        valHidden.append(0)
        weightHO.append(0)

    for i in range(0, nInputs):
        weightIH.append([])        
        for h in range(0, nHidden):
            weightIH[i].append(0)
    
    nPatterns = 0
    for line in open("nntrain.txt"):
        t = line.split(" ")
        tt = []
        for i in (0, 1, 2):
            tt.append(float(t[i]))
        trainInput.append(tt)
        trainOutput.append(float(t[3]))
        nPatterns += 1

def check():
    error = 0
    for p in range(0, nPatterns):
        e  = calcNN(p)
        error += e ** 2
    
    error /= nPatterns
    
    print "Error :", math.sqrt(error)
        

def main():
    initData()    
    initWeights()
    
    for r in range(0, nRounds):
        p = random.randint(0, 3)
#        p = r % 4
        error = calcNN(p)
        changeWeightHO(error)
        changeWeightIH(p, error)
#        print trainInput[p][0], trainInput[p][1], trainInput[p][2]
        print "Round", r, "Error = ", error
        
    check()
main()
            
    