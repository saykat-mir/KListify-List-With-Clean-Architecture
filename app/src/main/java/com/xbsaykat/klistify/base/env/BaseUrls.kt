import com.xbsaykat.klistify.BuildConfig
import com.xbsaykat.klistify.base.utils.Types.BuildType

private val currentEnv = if (BuildConfig.DEBUG) BuildType.STAGE
else BuildType.PRODUCTION

val CurrentBuildType = currentEnv.name

val BASE_URL = when (currentEnv) {
    BuildType.PRODUCTION -> BuildConfig.baseurl
    BuildType.STAGE -> BuildConfig.stage_baseurl
    BuildType.DEV -> BuildConfig.dev_baseurl
    else -> BuildConfig.baseurl
}
