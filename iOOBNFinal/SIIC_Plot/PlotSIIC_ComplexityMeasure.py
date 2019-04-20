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

def myPlot(df, xaxis, yaxis, xLimMin, xLimMax, yLimMin, yLimMax, ax, gridQ=False, plotQ=True, param='', paramValue=0):
    if plotQ == True:
        ax = df[[xaxis, yaxis]].plot(x = xaxis, y=yaxis, grid=gridQ, ax=ax)
    else:
        ax = df[[xaxis, yaxis]].plot.scatter(x=xaxis, y=yaxis, grid=gridQ, ax=ax)

    plt.xlim([xLimMin, xLimMax])  # complexity
    plt.ylim([yLimMin, yLimMax])  # difference

    # xtick = pd.date_range( start=df.index.min( ), end=df.index.max( ), freq='W' )
    # ax.set_xticks( xtick, minor=True )
    # ytick = pd.date_range( start=df.index.min( ), end=df.index.max( ), freq='W' )
    # ax.set_yticks( ytick, minor=True )

    # ax.legend()
    # plt.show()
    plt.savefig(xaxis+'_'+yaxis+'_'+param+'_'+str(paramValue)+".pdf", dpi=800)

# ax = df[['complexity','difference']].plot.scatter(x = 'complexity', y='difference', grid=True)
ax = plt.gca()

myPlot(df, 'complexity', 'difference', 0, 3000, -20, 20, ax, True, False, '', '')
plt.show()

ax1 = plt.gca()
for n in set(NON):
    myPlot(df.loc[df["NON"]==n], 'complexity', 'difference', 0, 3000, -20, 20, ax1, True, False, 'NumOfNode', n)
plt.show()

ax2 = plt.gca()
for n in set(NOC):
    myPlot(df.loc[df["NOC"]==n], 'complexity', 'difference', 0, 3000, -20, 20, ax2, True, False, 'NumOfClass', n)
plt.show()

ax3 = plt.gca()
for n in set(NOO):
    myPlot(df.loc[df["NOO"]==n], 'complexity', 'difference', 0, 3000, -20, 20, ax3, True, False, 'NumOfObj', n)
plt.show()

ax4 = plt.gca()
for n in set(NOS):
    myPlot(df.loc[df["NOS"]==n], 'complexity', 'difference', 0, 3000, -20, 20, ax4, True, False, 'NumOfState', n)
plt.show()

ax5 = plt.gca()
for n in set(NOPar):
    myPlot(df.loc[df["NOPar"]==n], 'complexity', 'difference', 0, 3000, -20, 20, ax5, True, False, 'NumOfPar', n)


# plt.savefig('complexityVSdifference.pdf', dpi=800)
plt.show()

df.to_csv('output.csv', '\t')
print("Done plotting")
