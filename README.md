# Apiloop Workers

![Workers](resources/workers.png)

## Overview

This library was part of the [Apiloop](https://www.apiloop.io) platform (_now shutdown_ 😭). 

It defines a set of [workers](src/main/java/io/apiloop/workers/base/Worker.java) that were usable in the workflows.

For each [BusinessObject](src/main/java/io/apiloop/workers/base/BusinessObject.java) that you had in your data model (User, Post, etc..)
you could hook on lifecycle events (on create, on delete, on update, etc.) to perform specific actions.
 
These actions had an entry predicate (whether to execute it or not) and a worker.

The job of the worker is simple : *do one thing, and do it well*.

## Workers

Here is a list of the workers currently implemented. The workers were implemented on demand, but the idea is to add more and more.

| Name                     | Description                                |
|--------------------------|--------------------------------------------|
| AlgoliaObjectIndexer     | Index an object in Algolia                 |
| FirmApiCompanyInfoGetter | Get a French company information           |
| MailChimpListMemberAdder | Add a member to a MailChimp list           |
| MessageBirdSMSSender     | MessageBirdSMSSender                       |
| SendGridEmailSender      | Send an SMS via MessageBird                |
| SMSPartnerSMSSender      | Send an email via SendGrid                 |
| StripeTokenCharger       | Send an SMS via SMS Partner                |
| TextRazorTextAnalyzer    | Charge a credit card with Stripe           |
| ObjectDisabler           | Disable a resource                         |
| ObjectFieldPopulator     | Populate an attribute with a default value |
| ObjectFieldsBCryptHasher | Crypt data                                 |
| ObjectFieldsCapitalizer  | Capitalize the first letter                |
| ObjectFieldSlugifier     | Make an attribute URL-friendly             |
| ObjectEnabler            | Enable a resource                          |
| ObjectSaver              | Save a resource                            |

## Contribute

### Execute the tests

```shell
docker run -it --rm \
    -v $(pwd):/root \
    hseeberger/scala-sbt:8u222_1.3.5_2.13.1 \
    sbt test
```

### Build

```shell
docker run -it --rm \
    -v $(pwd):/root \
    hseeberger/scala-sbt:8u222_1.3.5_2.13.1 \
    sbt package
```

Integrate the generated `jar` file in your project using you favorite build tool.

## License

Apache License 2.0
