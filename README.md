﻿# INEZ - Der INtelligente EinkaufsZettel

| Branch  |                                                                                                                                 |
| ------- | ------------------------------------------------------------------------------------------------------------------------------- |
| develop | [![Build Status](https://travis-ci.com/p85/ines.svg?token=WBCzZARApxyW7X3Upy1E&branch=develop)](https://travis-ci.com/p85/ines) |
| master  | [![Build Status](https://travis-ci.com/p85/ines.svg?token=WBCzZARApxyW7X3Upy1E&branch=master)](https://travis-ci.com/p85/ines)  |


My Entry for the [IT-Talents Code Competition](https://www.it-talents.de/foerderung/code-competition/edeka-digital-code-competition-08-2019).

## Requirements
	 - node (tested with 8.9.0)
	 - yarn (tested with 1.17.3)
## Used external Packages
- [shadow-cljs](http://shadow-cljs.org/)
- [reagent](https://reagent-project.github.io/)
- [stylelint](https://stylelint.io/)
- [Bootstrap](https://getbootstrap.com/)
- [Bytesize Icons](https://github.com/danklammer/bytesize-icons/)
## Pre-Compiled Version
The Folder `dist` contains a Pre-Compiled Version which can be started right away.

## Installation
    clone this repo (duh)
    yarn deps
## Run the Tests

    yarn test

## Build
This will compile the Program into JavaScript.

The Output Folder is "dist".

### **This is not needed** when you use `yarn server` or `yarn watch`

    yarn release
## Start

    yarn server
This will compile the Program into JavaScript and start a local WebServer listening on [localhost:8700](http://127.0.0.1:8700/) which serves the App.
## Development

    yarn watch
Same as `yarn server` but will live recompile the changes in the Browser.
