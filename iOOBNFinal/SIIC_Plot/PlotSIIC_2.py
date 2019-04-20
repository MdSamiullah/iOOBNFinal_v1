import matplotlib.pyplot as plt
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


def complexityOfOneBN(NON, NOS, NOP):
    complexity = 0
    for n in range(NON):
        nodeComplexity = NOS - 1
        for p in range(NOP):
            nodeComplexity *= NOS
        complexity += nodeComplexity

    return complexity


def calculateComplexityOOBN(NOO, NOC, NON, NOS, NOP):#
    NOBN = NOO * NOC # NOBN = encapsulated object bns
    overallComplexity = 0
    for bn in range(NOBN+1): # +1 for the compiling class
        overallComplexity += complexityOfOneBN(NON, NOS, NOP)

    return overallComplexity


def addComplexityTimeDifference(dataTable):
    countSIICWin = 0
    complexityList = []
    timeDifferenceList = []

    NONLL = dataTable.get("NON")
    NOOLL = dataTable.get("NOO")
    NOCLL = dataTable.get("NOC")
    NOPLL = dataTable.get("NOPar")
    NOSLL = dataTable.get("NOS")
    Hugin = dataTable.get("Hugin")
    SIIC = dataTable.get("SIIC")

    for i in range(len(NONLL)):
        complexity = calculateComplexityOOBN(NOOLL[i], NOCLL[i], NONLL[i], NOSLL[i], NOPLL[i])
        complexityList.append(complexity)
        timeDifferenceList.append(Hugin[i]-SIIC[i])
        if Hugin[i] > SIIC[i]:
            countSIICWin += 1

    dataTable["complexity"] = complexityList
    dataTable["difference"] = timeDifferenceList

    print('SIIC wins ', countSIICWin, ' among total cases ', len(NONLL))
    print('overall difference is ')
    if(sum(timeDifferenceList) < 0):
        print('negative, so Hugin wins')
    else:
        print('positive, so SIIC wins')

    return dataTable


NON, NOPar, NOS , NOC, NOO, Hugin, SIIC = getDataFromFile("SIIC_Output_tabular_Combined.txt")

dataTable = dict()
dataTable["NON"] = NON
dataTable["Hugin"] = Hugin
dataTable["SIIC"] = SIIC
dataTable["NOPar"] = NOPar
dataTable["NOO"] = NOO
dataTable["NOS"] = NOS
dataTable["NOC"] = NOC

dataTable = addComplexityTimeDifference(dataTable)
# print(dataTable)

df = pd.DataFrame.from_dict(dataTable)
print(df)


def myPlot(df, xaxis, yaxis, xAxTitle, yAxTitle, xLimMin, xLimMax, yLimMin, yLimMax, ax, gridQ=False, plotQ=True, param='', paramValue=0):
    if plotQ == True:
        # ax = df[[xaxis, yaxis]].plot(x = xaxis, y=yaxis, grid=gridQ, ax=ax)
        ax = df[[xaxis, yaxis]].plot(x=xaxis, y=yaxis, grid=gridQ)
    else:
        # ax = df[[xaxis, yaxis]].plot.scatter(x=xaxis, y=yaxis, grid=gridQ, ax=ax)
        ax = df[[xaxis, yaxis]].plot.scatter(x=xaxis, y=yaxis, grid=gridQ)

    plt.xlim([xLimMin, xLimMax])  # complexity
    plt.ylim([yLimMin, yLimMax])  # difference

    ax.set_xlabel(xAxTitle)
    ax.set_ylabel(yAxTitle)
    # xtick = pd.date_range( start=df.index.min( ), end=df.index.max( ), freq='W' )
    # ax.set_xticks( xtick, minor=True )
    # ytick = pd.date_range( start=df.index.min( ), end=df.index.max( ), freq='W' )
    # ax.set_yticks( ytick, minor=True )

    # ax.legend()
    # plt.show()
    plt.savefig(xaxis+'_'+yaxis+'_'+param+'_'+str(paramValue)+".pdf", dpi=800)

# ax = df[['complexity','difference']].plot.scatter(x = 'complexity', y='difference', grid=True)
ax = plt.gca()

# myPlot(df, 'complexity', 'difference', 0, 3000, -20, 20, ax, True, False, '')
#
# myPlot(df, 'NON', 'difference', 0, 50, -20, 20, ax, True, False, 'NumOfNode')
# myPlot(df, 'NOC', 'difference', 0, 3, -20, 20, ax, True, False, 'NumOfClass')
# myPlot(df, 'NOO', 'difference', 0, 3, -20, 20, ax, True, False, 'NumOfObj')
# myPlot(df, 'NOS', 'difference', 0, 5, -20, 20, ax, True, False, 'NumOfState')
# myPlot(df, 'NOPar', 'difference', 0, 5, -20, 20, ax, True, False, 'NumOfPar')


myPlot(df, 'complexity', 'difference', 'BN Parameter Complexity', 'Time Difference (ms)', 0, 30000, -1000, 1000, ax, True, False, '')

myPlot(df, 'NON', 'difference', 'Number of Nodes Per class', 'Time Difference (ms)', 0, 50, -20, 20, ax, True, False, 'NumOfNode')
myPlot(df, 'NOC', 'difference', 'Number of additional class', 'Time Difference (ms)', 0, 3, -20, 20, ax, True, False, 'NumOfClass')
myPlot(df, 'NOO', 'difference', 'Number of Objects Per additional class', 'Time Difference (ms)', 0, 3, -20, 20, ax, True, False, 'NumOfObj')
myPlot(df, 'NOS', 'difference', 'Number of States Per Node', 'Time Difference (ms)', 0, 5, -20, 20, ax, True, False, 'NumOfState')
myPlot(df, 'NOPar', 'difference', 'Number of Parents Per Node', 'Time Difference (ms)', 0, 5, -20, 20, ax, True, False, 'NumOfPar')

myPlot(df.sort_values('complexity'), 'complexity', 'Hugin', 'Complexity', 'Hugin', 0, 38000, 0, 6000, ax, True, True, 'HuginVsComplexity')
myPlot(df.sort_values('complexity'), 'complexity', 'SIIC', 'Complexity', 'SIIC', 0, 38000, 0, 200, ax, True, True, 'SIICVsComplexity')

def printStatistics(param, paramVal):
    count = 0
    total = 0
    print("Stat for ", param, " for value ", paramVal, end=" ")
    for index, row in df.iterrows():
        if row[param] == paramVal:
            total += 1
            if row['difference'] < 0:
                count += 1
        # print (row["NON"], row["difference"])
    print('SIIC = ', total-count, ' Hugin = ', count)


printStatistics('NON', 5)
printStatistics('NON', 20)
printStatistics('NON', 50)

printStatistics('NOC', 0)
printStatistics('NOC', 1)
printStatistics('NOC', 2)
printStatistics('NOC', 3)

printStatistics('NOS', 2)
printStatistics('NOS', 3)
printStatistics('NOS', 4)

printStatistics('NOPar', 2)
printStatistics('NOPar', 3)
printStatistics('NOPar', 5)

printStatistics('NOO', 1)
printStatistics('NOO', 2)
printStatistics('NOO', 3)

numOfNodeList = [5, 20, 50]
arity = [2, 3, 4]
maxParent = [2, 3, 5]
additionalClass = [0, 1, 2, 3]
numObjPerAdditionalClass = [1, 2, 3]

# plt.savefig('complexityVSdifference.pdf', dpi=800)
plt.show()

df.to_csv('output.csv', ',')
print("Done plotting")
