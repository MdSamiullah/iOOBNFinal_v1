from mpl_toolkits.mplot3d import Axes3D
import matplotlib.pyplot as plt
from matplotlib import cm
from matplotlib.ticker import LinearLocator, FormatStrFormatter
import numpy as np
import pandas as pd


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

ax = plt.gca()
def myPlot(df, xaxis, yaxis, yaxis2, attrDict):
    # fig = plt.figure()
    # ax = fig.gca()
    # ax.plot(X, Y1, c='Red', label='Hugin')
    # ax.scatter(X, Y1, c='Red')
    # ax.plot(X, Y2, c='Blue', label='SIIC')
    # ax.scatter(X, Y2, c='Blue')
    # plt.xticks(X, X)
    # plt.ylim([0, 10])
    # plt.yticks(np.arange(Y1.min(), 10, 1))


    ax = df[[xaxis, yaxis, yaxis2]].plot(x = xaxis)
    # df[[xaxis, yaxis, yaxis2]].plot(x=xaxis, ax=ax)
    #x-axis title
    title = setXaxisTitle(ax, xaxis, attrDict)

    ax.set_ylabel("Time (ms)")
    ax.legend()
    # plt.show()
    plt.savefig(title+".pdf", dpi=600)


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

# print(df)
# print(df.loc[df["NON"]==20])

# myPlot(df, 'NON', 'SIIC', 'Hugin')
# myPlot(df.loc[df["NOO"]==3], 'NON', 'SIIC', 'Hugin')

numOfNodeList = [5, 20, 50]
arity = [2, 3, 4]
maxParent = [2, 3, 5]
additionalClass = [0, 1, 2, 3]
numObjPerAdditionalClass = [1, 2, 3]

# Phase 1 : for fixed NON = 5, 20, arity = 3, states = 3
    # NOC = 0, 1, 2, 3
    # NOO = 1, 2, 3
# Phase 2 : for fixed NOO = 2, NOC = 2
    # NON = 5, 20, 50
    # NOPar = 2, 3, 5
    # arity = 2, 3

# phase 1
NONL = 5
NOSL = 3
NOParL = 3
NOOL = 2# in case of varying NOC, the fixed num of object should come here
NOCL = 2# in case of varying NOC, the fixed num of object should come here
NOCLL = [0, 1, 2, 3]
NOOLL = [1, 2, 3]

dfOrg = df.copy()

attrDict = dict()
attrDict['NON'] = NONL
attrDict['NOC'] = None
attrDict['NOO'] = NOOL
attrDict['NOPar'] = NOParL
attrDict['NOS'] = NOSL

myPlot(df.loc[(df["NON"] == NONL) & (df["NOS"] == NOSL) & (df["NOPar"] == NOParL) & (df["NOO"] == NOOL)], 'NOC', 'SIIC', 'Hugin', attrDict)
attrDict['NOC'] = NOCL

attrDict['NOO'] = None
myPlot(df.loc[(df["NON"] == NONL) & (df["NOS"] == NOSL) & (df["NOPar"] == NOParL) & (df["NOC"] == NOCL)], 'NOO', 'SIIC', 'Hugin', attrDict)
attrDict['NOO'] = NOOL

# phase 2
NONLL = [5, 20, 50]
NOSLL = [2, 3]
NOParLL = [2, 3, 5]
NONL = 20
NOSL = 3
NOParL = 3
NOCL = 2
NOOL = 2


# for a in NONLL:
#     try:

attrDict = dict()
attrDict['NON'] = None
attrDict['NOC'] = NOCL
attrDict['NOO'] = NOOL
attrDict['NOPar'] = NOParL
attrDict['NOS'] = NOSL
myPlot(df.loc[(df["NOS"] == NOSL) & (df["NOPar"] == NOParL) & (df["NOO"] == NOOL) & (df["NOC"] == NOCL)], 'NON', 'SIIC', 'Hugin', attrDict)
attrDict['NON'] = NONL


attrDict['NOS'] = None
myPlot(df.loc[(df["NON"] == NONL) & (df["NOPar"] == NOParL) & (df["NOO"] == NOOL) & (df["NOC"] == NOCL)], 'NOS', 'SIIC', 'Hugin', attrDict)
attrDict['NOS'] = NOSL

attrDict['NOPar'] = None
myPlot(df.loc[(df["NON"] == 5) & (df["NOS"] == NOSL) & (df["NOO"] == NOOL) & (df["NOC"] == NOCL)], 'NOPar', 'SIIC', 'Hugin', attrDict)
attrDict['NOPar'] = NOParL

plt.show()


print("Done plotting")
