#!/bin/bash

if [[ $TRAVIS_PULL_REQUEST == "false" ]] && [[ $TRAVIS_BRANCH == "master" ]]; then
    echo $PASSPHRASE | gpg --output $GPG_SECRETKEYRING --batch --passphrase-fd 0 --decrypt encrypted-maven.gpg
    ./mvnw --settings settings.xml clean deploy \
        -Dgpg.keyname=$GPG_KEYNAME \
        -Dgpg.passphrase=$GPG_PASSPHRASE \
        -Dgpg.secretKeyring=$GPG_SECRETKEYRING
fi
