# Maven Central Publishing & CI/CD Plan

This document provides a professional-grade setup for publishing Kotlin Multiplatform (KMP) or Android libraries to Maven Central using the `vanniktech/gradle-maven-publish-plugin` and GitHub Actions.

## 1. Prerequisites: GPG & Maven Central Secrets

Ensure the following 4 secrets are added to your GitHub Repository (**Settings → Secrets and variables → Actions**):

| Secret Name | Description |
| :--- | :--- |
| `MAVEN_CENTRAL_USERNAME` | Your Sonatype/Jira username. |
| `MAVEN_CENTRAL_PASSWORD` | Your Sonatype/Jira password (or Token). |
| `SIGNING_KEY` | ASCII-armored GPG secret key block. |
| `SIGNING_PASSWORD` | The passphrase for your GPG key. |

### How to get your GPG Key block:
Run this on your local machine:
```bash
# Export the ASCII-armored secret key
gpg --export-secret-keys --armor <YOUR_KEY_ID>
```
Copy the *entire* block (including `-----BEGIN PGP PRIVATE KEY BLOCK-----`) into the `SIGNING_KEY` secret.

---

## 2. Gradle Configuration

### A. `gradle/libs.versions.toml`
```toml
[versions]
maven-publish = "0.33.0"

[plugins]
maven-publish = { id = "com.vanniktech.maven.publish", version.ref = "maven-publish" }
```

### B. Root `build.gradle.kts`
```kotlin
plugins {
    alias(libs.plugins.maven.publish) apply false
}
```

### C. Module `build.gradle.kts` (e.g., `:library`)
```kotlin
plugins {
    alias(libs.plugins.maven.publish)
}

mavenPublishing {
    // 1. Update these to your new project details
    coordinates(
        groupId = "io.github.kabirnayeem99", 
        artifactId = "your-new-project-id",
        version = "1.0.0",
    )

    // 2. POM Metadata
    pom {
        name.set("Project Name")
        description.set("A deep description of what this project does.")
        inceptionYear.set("2026")
        url.set("https://github.com/kabirnayeem99/YourNewProject")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("kabirnayeem99")
                name.set("Naimul Kabir")
                email.set("kabirnayeem.99@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/kabirnayeem99/YourNewProject")
            connection.set("scm:git:git://github.com/kabirnayeem99/YourNewProject.git")
            developerConnection.set("scm:git:ssh://github.com/kabirnayeem99/YourNewProject.git")
        }
    }

    // 3. Publishing Destination
    publishToMavenCentral() 

    // 4. Signing (uses the in-memory keys from CI)
    signAllPublications()
}
```

---

## 3. GitHub Actions Workflow
Create a file at `.github/workflows/publish.yml`:

```yaml
name: Publish

on:
  release:
    types: [ published ]
  workflow_dispatch:

jobs:
  publish:
    name: Publish to Maven Central
    # Use 'macos-latest' for Apple/iOS targets, 'ubuntu-latest' for JVM/Android only.
    runs-on: macos-latest 
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'

      - name: Set up Gradle
        uses: gradle/actions/setup-gradle@v4

      - name: Publish and release
        run: ./gradlew :your-module:publishAndReleaseToMavenCentral --no-configuration-cache --console=plain
        env:
          ORG_GRADLE_PROJECT_mavenCentralUsername: ${{ secrets.MAVEN_CENTRAL_USERNAME }}
          ORG_GRADLE_PROJECT_mavenCentralPassword: ${{ secrets.MAVEN_CENTRAL_PASSWORD }}
          ORG_GRADLE_PROJECT_signingInMemoryKey: ${{ secrets.SIGNING_KEY }}
          ORG_GRADLE_PROJECT_signingInMemoryKeyPassword: ${{ secrets.SIGNING_PASSWORD }}
```

---

## 4. Verification Checklist
1. **Module Name:** Replace `:your-module` in the workflow with your actual module name (e.g., `:library`).
2. **Namespace:** The `groupId` must be verified in your Sonatype account.
3. **Release Trigger:** The workflow triggers when you create a new **GitHub Release**. Ensure the `version` in `build.gradle.kts` matches your release tag.
4. **First Run:** Use `workflow_dispatch` (Run workflow button in Actions tab) to test the build before doing a full release.
