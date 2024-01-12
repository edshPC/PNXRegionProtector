# PNXRegionProtector
An adapted release of [SRegionProtector](https://github.com/SergeyDertan/SRegionProtector) for [PowerNukkitX](https://github.com/PowerNukkitX/PowerNukkitX).

Currently have some problems with database provider external libs (datanucleus) so only YAML storage is allowed.
Will be added through DBLib plugin in future.

SRegionProtector is a nukkit plugin that allows players to protect their regions.
## Download
* [Release](https://github.com/edshPC/PNXRegionProtector/releases/latest)
## Features
* Chest and form UI with custom page support
* API for another plugins
* Lots of flags
* Flexible settings
* High performance
## Maven dependency
<details>
<summary>Maven dependency</summary>

```
<repositories>
    <repository>
        <id>SRegionProtector-master</id>
        <url>https://raw.github.com/SergeyDertan/SRegionProtector/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>sergeydertan.sregionprotector</groupId>
        <artifactId>SRegionProtector</artifactId>
        <version>LATEST</version>
    </dependency>
</dependencies>
```

</details>

## Commands and permissions can be found at [Wiki](https://github.com/SergeyDertan/SRegionProtector/wiki).

## !Warning! some flags may not work because they are disabled, pls check config.yml first
