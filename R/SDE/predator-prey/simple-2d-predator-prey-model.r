
dx.dt <- function(x, y, K)
{
x *(1 - x/K - 4*y/(1+x))
}

dy.dt <- function(x, y, K)
{
y * (-1 + 2*x/(1+x))
}

dx.dt.line <- function(x, K)
{
(1+x - (1+x)*x/K)/4
}


iter <- function(x.and.y, K, noise, timestep)
{
x <- x.and.y$x
y <- x.and.y$y
x.new <- x + dx.dt(x, y, K) * timestep
x.new <- x.new * rlnorm(1, meanlog=0, sdlog=noise)
y.new <- y +  dy.dt(x, y, K) * timestep 
y.new <- y.new * rlnorm(1, meanlog=0, sdlog=noise)
list(x=x.new, y=y.new)
}


make.path <- function(x, y, K, noise, nofiters, timestep)
{
x.path <- x
y.path <- y
for (i in 1:nofiters)
  {
  p <- iter(list(x=x, y=y), K, noise, timestep)
  x <- p$x
  y <- p$y
  x.path <- c(x.path, x)
  y.path <- c(y.path, y)
  }
list(x.path=x.path, y.path=y.path)
}


plot.separatrix <- function(K) 
{
xx <- seq(0, K, length.out=201)
yy <- dx.dt.line(xx, K)
lines(xx,yy, col="grey")
abline(v=1, col="grey")
}


case.cycle <- function()
{
K <- 3.5
timestep <- .1
inner <- make.path(1, .17, K, 0, 250, timestep)
outer <- make.path(2, 1, K, 0, 250, timestep)
limit <- make.path(1, .7328, K, 0, 129, timestep)
plot(NULL, NULL, xlim=c(0,K), ylim=c(0,1.2), type="l", xlab="x", ylab="y", main="K=3.5. Limit cycle")
plot.separatrix(K)
lines(outer$x.path, outer$y.path, col="red")
lines(inner$x.path, inner$y.path, col="blue")
lines(limit$x.path, limit$y.path, col="black", lty="dashed")
w <- which((1:length(outer$x.path) %% 50 == 1))
text(outer$x.path[w], outer$y.path[w], as.character(1:length(w)), col="black")
w <- which((1:length(inner$x.path) %% 50 == 1))
text(inner$x.path[w], inner$y.path[w], as.character(1:length(w)), col="black")
}


case.point <- function()
{
K <- 2.5
timestep <- .1
path <- make.path(2, 1, K, 0, 500, timestep)
plot(NULL, NULL, xlim=c(0,K), ylim=c(0,1.2), type="l", xlab="x", ylab="y", main="K=2.5. Stable equilibrium point")
plot.separatrix(K)
lines(path$x.path, path$y.path, col="red")
w <- which((1:length(path$x.path) %% 50 == 1))
text(path$x.path[w], path$y.path[w], as.character(1:length(w)), col="black")
}



case.noisy <- function()
{
set.seed(2)
K <- 2.5
timestep <- .1
path <- make.path(1, .2, K, .03, 250, timestep)
plot(NULL, NULL, xlim=c(0,K), ylim=c(0,1.2), type="l", xlab="x", ylab="y", main="K=2.5 with noise")
plot.separatrix(K)
lines(path$x.path, path$y.path, col="red")
w <- which( (1:length(path$x.path)) %% 25 == 1)
text(path$x.path[w], path$y.path[w], as.character(1:length(w)), col="black")
}    


case.point()
case.cycle()
case.noisy()


K <- 2.5
timestep <- .1
path <- make.path(1, .2, K, .05, 2000, timestep)
times <- (1:length(path$x.path))*timestep
plot(times, (path$y.path - mean(path$y.path))/sd(path$y.path), type="l", ylab="x and y, normalised", xlab="time", col="grey")
lines(times, (path$x.path - mean(path$x.path))/sd(path$x.path))
abline(h=0)     

