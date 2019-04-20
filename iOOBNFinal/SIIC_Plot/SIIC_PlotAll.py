import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

pos = 0

def getDataFromFile(fileName):
    file = open(fileName, "r+")
    NON = [] # num of nodes
    NOPar = []  # num of parents
    NOS = [] # num of states
    NOC = [] # num of class
    NOO = [] # num of objects
    Hugin = [] # hugin time
    SIIC = [] # siic time

    for line in file:
        # print(line)
        if line.startswith("NumOfNodes"):
            pass
        else:
            line = line.split()
            NON.append(int(line[0]))
            NOPar.append(int(line[1]))
            NOS.append(int(line[2]))
            NOC.append(int(line[3]))
            NOO.append(int(line[4]))
            Hugin.append(int(line[5]))
            SIIC.append(int(line[6]))

    return NON, NOPar, NOS , NOC, NOO, Hugin, SIIC

def makeTitleTail(attrDict):
    TITLE = "( "
    doneComma = False
    for key in attrDict:
        if attrDict.get(key) != None:
            if doneComma == True:
                TITLE +=  ", " + key + " = " + str(attrDict.get(key))
            else:
                TITLE += key + " = " + str(attrDict.get(key))
                doneComma = True
    TITLE += " )"

    return TITLE


def setXaxisTitle(ax, xaxis, attrDict):
    tailTitle = makeTitleTail(attrDict)
    if xaxis == 'NOC':
        ax.set_xlabel("Number of additional classes " + tailTitle)
        return "Number of additional classes " + tailTitle
    elif xaxis == 'NOO':
        ax.set_xlabel("Number of Object per additional class" + tailTitle)
        return "Number of Object per additional class" + tailTitle
    elif xaxis == 'NON':
        ax.set_xlabel("Number of nodes per class" + tailTitle)
        return "Number of nodes per class" + tailTitle
    elif xaxis == 'NOS':
        ax.set_xlabel("Number of States per node" + tailTitle)
        return "Number of States per node" + tailTitle
    elif xaxis == 'NOPar':
        ax.set_xlabel("Avg. Number of Parents" + tailTitle)
        return "Avg. Number of Parents" + tailTitle


def myPlot(df, xaxis, yaxis, yaxis2, attrDict, Pos):
    # fig = plt.figure()
    # ax = fig.gca()
    # ax.plot(X, Y1, c='Red', label='Hugin')
    # ax.scatter(X, Y1, c='Red')
    # ax.plot(X, Y2, c='Blue', label='SIIC')
    # ax.scatter(X, Y2, c='Blue')
    # plt.xticks(X, X)
    # plt.ylim([0, 10])
    # plt.yticks(np.arange(Y1.min(), 10, 1))


    ax = df[[xaxis, yaxis, yaxis2]].plot(x = xaxis, ax=Pos)

    #x-axis title
    title = setXaxisTitle(ax, xaxis, attrDict)

    ax.set_ylabel("Time (ms)")
    ax.legend()
    plt.show()
    # plt.savefig(title+".pdf", dpi=600)


def performPlotting(paramXAxis):
    params = set()

    for key in paramTable.keys():
        if key != paramXAxis:
            params.add(key)

    valueList = []
    paramList = []
    for param in params:
        valueList.append(paramTable.get(param))
        paramList.append(param)
    global pos
    for a in valueList[0]:
        for b in valueList[1]:
            for c in valueList[2]:
                for d in valueList[3]:
                    r = pos // 19
                    c = pos % 19
                    pos += 1
                    attrDict = dict()
                    attrDict[paramList[0]] = a
                    attrDict[paramList[1]] = b
                    attrDict[paramList[2]] = c
                    attrDict[paramList[3]] = d
                    attrDict[paramXAxis] = None
                    # print(attrDict)
                    try:
                        myPlot(df.loc[(df[paramList[0]] == a) & (df[paramList[1]] == b) & (df[paramList[2]] == c) & (df[paramList[3]] == d)], paramXAxis, 'SIIC', 'Hugin', attrDict, axes[r][c])
                    except:
                        continue


NON, NOPar, NOS , NOC, NOO, Hugin, SIIC = getDataFromFile("SIIC_Output_tabular_Combined.txt")

NON = np.array(NON)
NOPar = np.array(NOPar)
NOC = np.array(NOC)
NOO = np.array(NOO)
Hugin = np.array(Hugin)
SIIC = np.array(SIIC)

dataTable = dict()
dataTable["NON"] = NON
dataTable["Hugin"] = Hugin
dataTable["SIIC"] = SIIC
dataTable["NOPar"] = NOPar
dataTable["NOO"] = NOO
dataTable["NOS"] = NOS
dataTable["NOC"] = NOC

df = pd.DataFrame.from_dict(dataTable)


numOfNodeList = [5, 20, 50]
arity = [2, 3, 4]
maxParent = [2, 3, 5]
additionalClass = [0, 1, 2, 3]
numObjPerAdditionalClass = [1, 2, 3]


NOCLL = [0, 1, 2, 3]
NOOLL = [1, 2, 3]
NONLL = [5, 20, 50]
NOSLL = [2, 3, 4]
NOParLL = [2, 3, 5]

paramTable = dict()
paramTable["NON"] = NONLL
paramTable["NOPar"] = NOParLL
paramTable["NOO"] = NOOLL
paramTable["NOS"] = NOSLL
paramTable["NOC"] = NOCLL

fig, axes = plt.subplots(nrows=5, ncols=5) # to project 4x3x3x3x3=336 plots in rows and cols

ax = plt.gca()
for key in paramTable:
    performPlotting(key)
# performPlotting('NON')

plt.show()


print("Done plotting")
