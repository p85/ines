﻿# INEZ - Der INtelligente EinkaufsZettel
My Entry for the [IT-Talents Code Competition](https://www.it-talents.de/foerderung/code-competition/edeka-digital-code-competition-08-2019).
This Program is written in [ClojureScript](https://clojurescript.org/).
I have used this Code Competition to learn Clojure/ClojureScript. It was a great experience. It took me roughly two Weeks.
## Requirements
	 - node (tested with 8.9.0)
	 - yarn (tested with 1.17.3)
## Used external Packages
- [Bootstrap](https://getbootstrap.com/)
- [Bytesize Icons](https://github.com/danklammer/bytesize-icons/)
## Installation
    clone this repo
    yarn deps
## Run the Tests
On Linux:

    yarn test-linux

On Windows:

     yarn test-windows

## Build
(**This is optional**, it will build the HTML Files and compile the Program into JavaScript. **This is not needed** when you use `yarn server` or `yarn watch`)

    yarn release
## Start

    yarn server
This will compile the Program into JavaScript and start a local WebServer listening on [localhost:8700](http://127.0.0.1:8700/) which serves the Program.
## Development

    yarn watch
Same as `yarn server` and will live recompile the changes in the Browser.
