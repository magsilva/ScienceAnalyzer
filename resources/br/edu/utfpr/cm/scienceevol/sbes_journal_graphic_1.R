#graphic 1

years_frequency <- read.csv("/Users/Viny/Dropbox/Project SBES/Analysis/Graphics info/year_frequency.csv", header=TRUE)

x = barplot(years_frequency$Frequency, names.arg = years_frequency$Year, col="#6A9FD3",ylim=c(0,10))
y <- as.matrix(years_frequency$Frequency)
text(x,y+0.333,labels=as.character(y)) 
