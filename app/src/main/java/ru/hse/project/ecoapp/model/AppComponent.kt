package ru.hse.project.ecoapp.model

import android.content.Context
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import ru.hse.project.ecoapp.data.AWSClient
import ru.hse.project.ecoapp.data.Repository
import ru.hse.project.ecoapp.data.source.DataSourceMemory
import ru.hse.project.ecoapp.data.source.DataSourceService
import ru.hse.project.ecoapp.util.PicassoTools

class AppComponent(context: Context) {
    private val repository = Repository(DataSourceService(), DataSourceMemory(context))
    private val awsClient = AWSClient(context)
    private val picassoHttps = PicassoTools.getHttpsPicasso(context)
    private var activeFragment:Fragment? = null
    fun getRepository(): Repository {
        return repository
    }

    fun getAWSClient(): AWSClient {
        return awsClient
    }

    fun getPicassoHttps(): Picasso {
        return picassoHttps
    }

    fun getActiveFragment(): Fragment? {
        return activeFragment
    }

    fun setActiveFragment(fragment: Fragment){
        activeFragment=fragment
    }

}