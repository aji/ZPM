MCP="$1"
VERSION="$2"
VERSION_CLIENT="1.2.5"
VERSION_SERVER="1.2.5"

if [ -z "$MCP" -o -z "$VERSION" ]; then
	echo "usage: $0 PATH/TO/MCP VERSION"
	echo "example:"
	echo "  $0 ../1.2.5-forge 1.1dev"
	echo "    produces zpm-1.1dev-client-1.2.5.jar and zpm-1.1dev-server-1.2.5.jar in release/"
	echo ""
	echo "The following files are copied into the jar with the following mappings:"
	echo "  art/zpmtex.png => /net/ajitek/mc/zpm/block.png"
	echo "  mcmod.info => /mcmod.info"
	exit 1
fi

if [ ! -f "$MCP/conf/version.cfg" ]; then
	echo "$MCP doesn't seem to be an MCP directory"
	exit 1
fi

if [ ! -d "$MCP/reobf/minecraft" ]; then
	echo "$MCP doesn't seem to contain reobfuscated sources"
	echo "Did you forget to run recompile.sh/reobfuscate.sh?"
	exit 1
fi

DIR_MCP="$(readlink -f $MCP)"
DIR_ZPM="$(dirname $(readlink -f $0))"
DIR_RELEASE="$DIR_ZPM/release"

function getver {
	cd $DIR_MCP
	grep "$1" conf/version.cfg | awk '{print $3}' | sed 's/\r//'
}

VERSION_MCP=$(getver MCPVersion)
VERSION_CLIENT=$(getver ClientVersion)
VERSION_SERVER=$(getver ServerVersion)

[ "$VERSION_MCP" != "6.2" ] && echo "Not using MCP 6.2, things could get weird";
[ "$VERSION_CLIENT" != "1.2.5" ] && echo "Not using Minecraft Client 1.2.5, things could get weird";
[ "$VERSION_SERVER" != "1.2.5" ] && echo "Not using Minecraft Server 1.2.5, things could get weird";

JAR_CLIENT="$DIR_RELEASE/zpm-${VERSION}-client-${VERSION_CLIENT}.jar"
JAR_SERVER="$DIR_RELEASE/zpm-${VERSION}-server-${VERSION_SERVER}.jar"

# This is to ensure that we've changed out of the reobf/minecraft
# directory before attempting to run jar. I don't know why this would
# necessary (these bugs should have been fixed in the 80's), but jar
# complains if we don't, since MCP cleans reobf/ first.
cd /

mkdir -p "$DIR_RELEASE"

cd $DIR_MCP/reobf/minecraft
cp $DIR_ZPM/art/zpmtex.png net/ajitek/mc/zpm/block.png
cp $DIR_ZPM/mcmod.info mcmod.info
echo "Creating $JAR_CLIENT"
jar cf "$JAR_CLIENT" *

cd $DIR_MCP/reobf/minecraft_server
cp $DIR_ZPM/mcmod.info mcmod.info
echo "Creating $JAR_SERVER"
jar cf "$JAR_SERVER" *
