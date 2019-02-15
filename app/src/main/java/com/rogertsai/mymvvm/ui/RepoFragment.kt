package com.rogertsai.mymvvm.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.rogertsai.mymvvm.data.model.Repo
import com.rogertsai.mymvvm.databinding.RepoFragmentBinding
import com.rogertsai.mymvvm.viewmodel.GithubViewModelFactory

class RepoFragment : Fragment() {

    companion object {
        const val TAG = "Repo"

        fun newInstance(): RepoFragment {
            return RepoFragment()
        }
    }

    private lateinit var mBinding: RepoFragmentBinding
    private lateinit var mViewModel: RepoViewModel

    private var factory = GithubViewModelFactory()
    private var repoAdapter = RepoAdapter(ArrayList())


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = RepoFragmentBinding.inflate(inflater, container, false)

        mBinding.edtQuery.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if ((event!!.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    doSearch()
                    return true
                }
                return false
            }
        })

        mBinding.btnSearch.setOnClickListener { doSearch() }

        mBinding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerView.adapter = repoAdapter

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /* 不再用new而是改成透過ViewModelProviders協助我們取得ViewModel，
          其中of()的參數代表著ViewModel的生命範圍(scope)，
          在MainActivity中用of(this)表示ViewModel的生命週期會持續到MainActivity不再活動(destroy且沒有re-create)為止，
          只要MainActivity還在活動中，ViewModel就不會被清除，每次create都可以取得同一個ViewModel。 */
        mViewModel = ViewModelProviders.of(this, factory).get(RepoViewModel::class.java)
        mBinding.viewModel = mViewModel

        //使用observe(owner, Observer)來接收callback，owner用this表示LiveData會遵照MainActivity的生命週期判斷是否發送變更。
        mViewModel.getRepos().observe(this, Observer<MutableList<Repo>> { repos ->
            repoAdapter.swapItems(repos!!)
        })


    }

    private fun doSearch() {
        val query = mBinding.edtQuery.text.toString()

        if (TextUtils.isEmpty(query)) {
            repoAdapter.clearItems()
            return
        }
        mViewModel.searchRepo(query)
        dismissKeyboard()
    }

    private fun dismissKeyboard() {
        val view = activity?.currentFocus

        if (view != null) {
            val imm: InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


}