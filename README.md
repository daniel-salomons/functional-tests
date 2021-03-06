# POMS functional tests

These are functional integration tests for POMS and the NPO Frontend and Backend API.

This is used to test actual deployments of [POMS](https://poms.omroep.nl/), the [POMS Backend API](https://api.poms.omroep.nl), and the [POMS Frontend API](https://rs.poms.omroep.nl)

This is currently split up in 3 modules
* a shared module, this contains only code which is shared by the other modules but no actual tests
* a selenium module. These are pure frontend tests, and will use a headless browser.
* the other functional tests use API's only, and e.g. create or change data using the backend api and checks whether those changes arrive correctly in the frontend API. This in many cases also uses java clients which are implemented using the actual domain objects.


These tests are automaticly run every day on a [jenkins server](https://jenkins.vpro.nl/job/POMS%20Functional%20Tests/) (requires login)  on the 'dev' environments. At request the tests can also be run on the 'test' environment or even on production.

The tests require a configuration file `${USER.HOME}/conf/npo-functional-tests.properties` and the selenium
tests additionally require `${USER.HOME}/conf/npo-browser-tests.properties`. These files must contain the required credentials to be able to run the tests.  Default values can be found in src/main/test/resources of the relevant module.

## Remarks
If you want to run all tests, you need to check out this including git submodules. One of things which are tests is whether all examples of https://github.com/npo-poms/api/tree/master/examples execute without errors (and for a bunch of them extra checks are implemented)
```bash
git clone --recurse-submodules https://github.com/npo-poms/functional-tests.git
```
