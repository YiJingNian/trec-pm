#!/bin/bash

# auto-sklearn 0.4.0 depends on xgboost==0.7.post3, which compiles ONLY using gcc@5 (see https://github.com/dmlc/xgboost/issues/1501)
brew install gcc@5
env CC=gcc-5 CXX=g++-5 pip3 install xgboost==0.7.post3

# auto-sklearn 0.4.0 depends on pyrfr>=0.7,<0.8, which requires swig
brew install swig

# Install auto-sklearn (see https://automl.github.io/auto-sklearn/stable/installation.html)
curl https://raw.githubusercontent.com/automl/auto-sklearn/master/requirements.txt | xargs -n 1 -L 1 pip3 install

pip3 install auto-sklearn

# Install the classifier dependencies
pip3 install -r requirements.txt
