# duct-api-sample

As practice, I will create api server by using [duct](https://github.com/duct-framework/duct).

I will create a system like a simple Twitter.

ER:

![er](https://user-images.githubusercontent.com/1624680/41804851-0259a150-76d9-11e8-93d4-474b0bbbba61.png)

### run database

To run database (postgresql):

```sh
docker-compose up
```

### Environment

To begin developing, start with a REPL.

```sh
lein repl
```

Then load the development environment.

```clojure
user=> (dev)
:loaded
```

Run `go` to prep and initiate the system.

```clojure
dev=> (go)
:duct.server.http.jetty/starting-server {:port 3000}
:initiated
```

By default this creates a web server at <http://localhost:3000>.

When you make changes to your source files, use `reset` to reload any
modified files and reset the server.

```clojure
dev=> (reset)
:reloading (...)
:resumed
```

### Testing

in repl:

```clojure
user=> (dev)
:loaded
dev=> (test)
...
```

or, run next command:

```sh
lein test
```

## Legal

Copyright © 2018 @kbaba1001

## License

MIT License
