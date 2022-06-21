import csv

f = open("./country_vaccinations.csv", "r")
data = csv.reader(f)

next(data)
initData = next(data)

dailyHigh = 0
dailyLow = -1

highestDate = initData[2]
lowestDate = initData[2]


for row in data:
    if dailyHigh < int(row[7]):
        dailyHigh = int(row[7])
        highestDate = row[2]
       
    if dailyLow > int(row[7]):
        dailyLow = int(row[7])
        lowestData = row[2]

f.close()

print("가장 많이 접종된 날: "+ highestDate)
print("가장 적게 접종된 날: "+ lowestDate)

