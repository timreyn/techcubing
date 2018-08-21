#/bin/sh

PWD=$pwd

cd $(dirname $0)
bazel run :buildifier
cd third_party/grpc-java
git checkout -- .
cd $PWD
