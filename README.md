# Fragment
자동차(Activity)에서 전기모터(Fragment1)과 가솔린엔진(Fragment2)를 둘 다 가지고 
전기(View1)와 휘발유(View2)를 모두 사용할 수 있다.
만약 Activity가 자동차에서 자전거로 바꾼다면 Fragment또한 자전거용 전기모터,
자전거용 가솔린엔진 등으로 바뀔 수 잇다. 
Activiy를 변경하지 않아도, 쉽게 View를 변경할 수 있어서 많이들 사용하는 것이다.
#
## Implementation
* MainActivity
```
class MainActivity : AppCompatActivity() {

    lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.flt_container,TestFragment.newInstance("1","2"),"TEST_FRAGMENT").commit()
    }
}
```
* TestFragment
```
class TestFragment : BaseFragment<FragmentTestBinding>() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        bindView(inflater, container!!, R.layout.fragment_test)
        return mBinding?.root
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
}
```
