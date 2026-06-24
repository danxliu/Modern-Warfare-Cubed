{ pkgs, lib, config, inputs, ... }:

let
  gradleJdk = pkgs.temurin-bin-26;
  gameJdk = pkgs.temurin-bin-8;
in
{
  packages = [
    gradleJdk
    gameJdk
    pkgs.xrandr
    pkgs.fontconfig
  ];

  env = {
    GRADLE_OPTS = toString [
      "-Dorg.gradle.java.installations.paths=${gradleJdk},${gameJdk}"
      "-Dorg.gradle.java.installations.auto-detect=false"
      "-Dorg.gradle.java.installations.auto-download=false"
    ];

    LD_LIBRARY_PATH = lib.makeLibraryPath (with pkgs; [
      libXcursor
      libXrandr
      libXxf86vm
      libGL
      libXrender
      libXext
      libX11
      libXtst
      libXi
      fontconfig
    ]);
  };

  scripts."compile-jar".exec = "./gradlew build";
  scripts."run-client".exec = "./gradlew runClient";
}
