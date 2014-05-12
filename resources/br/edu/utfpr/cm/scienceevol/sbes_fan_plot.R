library(plotrix)
#64 (60.9%) -> unit testing
#10 + 16 (24.7) -> integration testing
#12 (11.4%) -> system testing
#3 (2.8%) regression testing
# slices <- c(59, (10+16), 11, 3)
slices <- c(60.9, 24.7, 11.4, 2.8)
lbls <- c("Unit (61,2%)", "Integration (24,5%)", "System (13,3%)", "Regression (1%)")
fan.plot(slices, labels = lbls, main="Studies According to Test Phase", col=c("#8CC9C1", "#5C7D78", "#13796A", "#2A4E49"))