# Fragment
자동차(Activity)에서 전기모터(Fragment1)과 가솔린엔진(Fragment2)를 둘 다 가지고 
전기(View1)와 휘발유(View2)를 모두 사용할 수 있다.
만약 Activity가 자동차에서 자전거로 바꾼다면 Fragment또한 자전거용 전기모터,
자전거용 가솔린엔진 등으로 바뀔 수 잇다. 
Activiy를 변경하지 않아도, 쉽게 View를 변경할 수 있어서 많이들 사용하는 것이다.

#
## Concept
* FragmentManager를 통해서 할 수 있는 일들.
    * findFragmentById()나, findFragmentTag()를 통해서 fragment를 얻어올 수 있다.
    * popBackStack()으로 backkey를 구현할 수 있다.
    * addOnBackStackChangedListener 등록이 가능하다.
    * add(), remove(), replace(), commit()이 호출 가능하다.
    * addToBackStack()을 통해 activity에서 관리하는 back stack 에 FragmentTransaction을 등록 가능하다.
    * fragment가 addToBackStack()을 통해서 remove되는 경우 stopped state, 그렇지 않을 경우 destroy state
    * commit() 전에 setTransition() 을 통해 animation을 줄 수 있다.
    * commit() 된 transaction은 바로 실행되지 않고, main thread에 schedule 되어 처리된다.
    * commit()은 activity가 state save하기 전에 이루어져야한다. state save 이후에 commit()이 불리면 안된다.
    * 만약 state save와 관계 없이 작동하려면 commitAllowingStateLoss()를 호출해야 한다.
#
## Implementation
* MainActivity
```
class MainActivity : AppCompatActivity(),TestFragment.FragmentEventListener {

    lateinit var mBinding: ActivityMainBinding

    companion object {
        const val FRAGMENT_TAG = "TEST_FRAGMENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if(savedInstanceState==null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.flt_container, TestFragment.newInstance("1", "2"), FRAGMENT_TAG).commit()
        }

        mBinding.apply {
            btnTest.setOnClickListener {
                val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
                fragment?.let{
                    (fragment as TestFragment).testMessage("테스트입니다.")
                }
            }
            btnReplace.setOnClickListener {
                val transaction = supportFragmentManager.beginTransaction()
                // transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                transaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
                transaction.replace(R.id.flt_container,ReplaceFragment.newInstance("1","2"))
                transaction.addToBackStack("BACK") // 스택으로 쌓아둠, 명시하지않으면 기존의 Fragment 제거.
                transaction.commit()
            }
        }
    }

    override fun onTestEvent(message: String) {
        Toast.makeText(MainActivity@this,message,Toast.LENGTH_SHORT).show()
    }

}
```
* TestFragment #1
```
class TestFragment : BaseFragment<FragmentTestBinding>() {

    private var param1: String? = null
    private var param2: String? = null

    private var listener:FragmentEventListener?=null

    interface FragmentEventListener{
        fun onTestEvent(message:String)
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onAttach(context: Context) {
        // The first to be fired!
        // Called when the fragment instance is associated with an Activity
        // This does not mean the Activity is fully intialized.
        Timber.d("onAttach")
        super.onAttach(context)
        if(context is FragmentEventListener){
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // is called when fragment should create its view object hierarchy, either dynamically or via XML layout inflation.
        bindView(inflater, container!!, R.layout.fragment_test)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Called right after onCreateView is called.
        // Note: onViewCreated is only called if the view returned from onCreateView() is non-null
        // Any view setup should occur, for example: view lookups and attaching view listeners.
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        // this method is called after the parent Activity's onCreate() method has completed.
        super.onActivityCreated(savedInstanceState)
    }


    override fun onDestroyView() {
        // The fragment is about to be destroyed - cleanup
        Timber.d("onDestroyView")
        super.onDestroyView()
    }

    override fun onDetach() {
        // is called when the fragment is no longer connected to the Activity
        // get rid of referencees from onAttach() - null them out - to prevent memory leaks
        super.onDetach()
        Timber.d("onDetach")
        // clean up!
        this.listener = null

    }

    fun testMessage(msg:String){
        mBinding?.tvTest?.text = msg
        listener?.onTestEvent("message")
    }

}
```
* TestFragment #2
```
class ReplaceFragment : BaseFragment<FragmentReplaceBinding>() {

    private var param1: String? = null
    private var param2: String? = null

    private var listener:FragmentEventListener?=null

    interface FragmentEventListener{
        fun onTestEvent(message:String)
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReplaceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onAttach(context: Context) {
        // The first to be fired!
        // Called when the fragment instance is associated with an Activity
        // This does not mean the Activity is fully intialized.
        Timber.d("onAttach")
        super.onAttach(context)
        if(context is FragmentEventListener){
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // is called when fragment should create its view object hierarchy, either dynamically or via XML layout inflation.
        bindView(inflater, container!!, R.layout.fragment_replace)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Called right after onCreateView is called.
        // Note: onViewCreated is only called if the view returned from onCreateView() is non-null
        // Any view setup should occur, for example: view lookups and attaching view listeners.
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        // this method is called after the parent Activity's onCreate() method has completed.
        super.onActivityCreated(savedInstanceState)
    }


    override fun onDestroyView() {
        // The fragment is about to be destroyed - cleanup
        Timber.d("onDestroyView")
        super.onDestroyView()
    }

    override fun onDetach() {
        // is called when the fragment is no longer connected to the Activity
        // get rid of referencees from onAttach() - null them out - to prevent memory leaks
        Timber.d("onDetach")
        super.onDetach()
        // clean up!
        this.listener = null

    }

    fun testMessage(msg:String){
        mBinding?.tvTest?.text = msg
        listener?.onTestEvent("message")
    }

}
```