NON = [50]
NOS = [2, 3, 4]
NOP = [2, 3, 5]
NOC = [0, 1, 2, 3]
NOO = [1, 2, 3]
fold = 5

file = open('args'+str(NON[0])+'.txt', "w+")

for c in NOC:
    if c > 0:
        for o in NOO:
            for s in NOS:
                for p in NOP:
                    for n in NON:
                        for i in range(fold):
                            iter = i + 1
                            print(c, n, o, p, s, iter)
                            file.write(str(c) + ' ' + str(n) + ' ' +  str(o) + ' ' +  str(p) + ' ' +  str(s) + ' ' +  str(iter)+'\n')
    else:
        o = c
        for s in NOS:
            for p in NOP:
                for n in NON:
                    for i in range(fold):
                        iter = i + 1
                        print(c, n, o, p, s, iter)
                        file.write(str(c) + ' ' + str(n) + ' ' +  str(o) + ' ' +  str(p) + ' ' +  str(s) + ' ' +  str(iter)+'\n')

file.close()