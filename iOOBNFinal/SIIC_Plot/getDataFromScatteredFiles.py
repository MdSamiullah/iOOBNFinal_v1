NON = [5, 20, 50]
NOS = [2, 3, 4]
NOP = [2, 3, 5]
NOC = [0, 1, 2, 3]
NOO = [1, 2, 3]
fold = 5


def makeParamStr(c, n, o, p, s, iter):
    # nClasses_0#nObjects_0#nStates_2#nNodes_5#maxInDeg_2#maxArcs_6
    str1 = 'nClasses_' + str(c) + '#nObjects_' + str(o) + '#nStates_' + str(s) + '#nNodes_' + str(n) + '#maxInDeg_' + str(p) + '#maxArcs_6_' + str(iter)
    return str1

paramList = []

for c in NOC:
    if c > 0:
        for o in NOO:
            for s in NOS:
                for p in NOP:
                    for n in NON:
                        for i in range(fold):
                            iter = i
                            # print(c, n, o, p, s, iter)
                            paramList.append(makeParamStr(c, n, o, p, s, iter))
    else:
        o = c
        for s in NOS:
            for p in NOP:
                for n in NON:
                    for i in range(fold):
                        iter = i
                        # print(c, n, o, p, s, iter)
                        paramList.append(makeParamStr(c, n, o, p, s, iter))

content = []
content.append('Fold   	 NumOfNodes     NumOfAvgPar 	 NumOfStates 	 NumOfClass 	   NumOfObj 	     RComplexity           Hugin-JTCost 	       SIIC-JTCost   	  Hugin   	     SIIC\n')
for x in paramList:
    dir = 'C:\\Users\msam34\\git\\iOOBNFinal_v1\\iOOBNFinal\\editor\\huginIntegration\\'
    try:
        file = open(dir+'SIIC_Output_tabular_'+x+'.txt', 'r+')
        if file is not None:
            for line in file:
                if line.startswith('(){}<>{}()'):
                    line = line.replace('(){}<>{}()', '')
                    line = line.split()
                    line[len(line)-2] = str(int(line[len(line)-2]) - int(line[len(line)-1])) # SIIC total is the 10th data SIIC preproc is the 11 th 10th-11th = 10th the real siic
                    content.append(line[:-1])
                else:
                    pass
    except:
        continue


fileOut = open('batchCombined.txt', 'w+')
fileOut.write(content[0])
print(content[0])
for x in range(len(content)-1):
    # print(x)
    for i in content[x+1]:
        print(i, end = "\t\t")
        fileOut.write(str(i) + "\t\t")
    fileOut.write("\n")
    print()

fileOut.close()