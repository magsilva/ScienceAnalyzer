#graphic 3

y_freq_by_cat <- read.csv("/Users/Viny/Dropbox/Project SBES/Analysis/Graphics info/year_frequency_by_category.csv", header=TRUE)
barplot(as.matrix(y_freq_by_cat[,2:26]), beside=FALSE, col=c("#FFA700", "#000000", "#C680FB", "#809CFB", "#FB8080", "#1D7D8C", "#D8DC30"), names.arg=c("1987", "1988", "1989", "1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999", "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011"), density=c(0,99,20,30,40,50,70), angle=c(0,75,33,180,70,90,45))

legend("topleft", legend=y_freq_by_cat$Year, fill=c("#FFA700", "#000000", "#C680FB", "#809CFB", "#FB8080", "#1D7D8C", "#D8DC30"), density=c(0,99,20,30,40,50,70), angle=c(0,75,33,180,70,90,45), bty="n")