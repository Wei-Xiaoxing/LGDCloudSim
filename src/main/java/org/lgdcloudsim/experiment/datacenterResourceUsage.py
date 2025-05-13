import sqlite3
import matplotlib.pyplot as plt

# 连接到 SQLite 数据库（如果数据库文件不存在，会自动创建）
conn = sqlite3.connect('./RecordDb/LGDCloudSim.db')

# 创建一个游标对象，用于执行 SQL 语句
cursor = conn.cursor()

# 查询数据
cursor.execute('SELECT * FROM instance')
rows = cursor.fetchall()

datacenterTimeValueMap=dict()
# timeValueMap=dict()
for row in rows:
    datacenterId=row[9]
    startTime=row[11]
    finishTime=row[12]
    usage=row[3]
    if startTime==-1:
        continue
    if not datacenterId in datacenterTimeValueMap:
        datacenterTimeValueMap[datacenterId]=dict()
    timeValueMap=datacenterTimeValueMap[datacenterId]
    if not startTime in timeValueMap:
        timeValueMap[startTime]=0
    timeValueMap[startTime]=timeValueMap[startTime]+usage
    if not finishTime in timeValueMap:
        timeValueMap[finishTime]=0
    timeValueMap[finishTime]=timeValueMap[finishTime]-usage
    # print(row)
sortedDatacenterId = sorted(datacenterTimeValueMap)
for datacenterId in sortedDatacenterId:
    timeValueMap=datacenterTimeValueMap[datacenterId]
    sortedTime = sorted(timeValueMap.keys())
    xList=[]
    yList=[]
    usage=0
    for time in sortedTime:
        usage+=timeValueMap[time]
        xList.append(time)
        yList.append(usage)

    # 创建折线图
    plt.plot(xList, yList, label="datacenter "+str(datacenterId))  # label为图例的标签，marker指定数据点的标记样式
# 显示图例
plt.legend()
plt.savefig('./src/main/java/org/lgdcloudsim/experiment/datacenterResourceUsageMine2.png')

# 关闭游标和连接
cursor.close()
conn.close()