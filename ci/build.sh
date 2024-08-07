#!/bin/bash
echo Initiating build ...

echo
echo -----------------------------------------------------
echo VERSION :: $FIATLUX_VERSION
echo BASE_REF :: $GITHUB_BASE_REF
echo BRANCH :: $GITHUB_REF_NAME
echo EVENT :: $GITHUB_EVENT_NAME
echo -----------------------------------------------------
echo

if [[ "$GITHUB_BASE_REF" == "master" ]];then
   echo Checking that the resulting tag does not exist

   if git rev-parse -q --verify "refs/tags/$FIATLUX_TAG" >/dev/null; then
      echo ERROR! Tag $FIATLUX_VERSION exist. Please modify pom and commit.
      exit 1
   fi
fi

if [[ "$GITHUB_EVENT_NAME" == "push" ]] && [[ "$GITHUB_REF_NAME" == "master" ]];then
   echo Packaging new release ...
   ./gradlew clean build check connectedCheck assemble packageRelease archiveZip publishReleaseApk -PprotocExec=${GITHUB_WORKSPACE}/ci/protoc
else 
   echo Running test ...
   ./gradlew clean build check connectedCheck -PprotocExec=${GITHUB_WORKSPACE}/ci/protoc
fi

