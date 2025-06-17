package com.example.wooriga

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.wooriga.databinding.FragmentRecipeAddBinding

class RecipeAddFragment : Fragment() {

    private var _binding: FragmentRecipeAddBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 툴바 설정
        val toolbar = view.findViewById<View>(R.id.custom_toolbar)
        val btnClose = toolbar.findViewById<ImageButton>(R.id.btn_close)
        val btnDone = toolbar.findViewById<TextView>(R.id.btn_done)
        val title = toolbar.findViewById<TextView>(R.id.tv_toolbar_title)

        title.text = "요리법 추가"

        // 닫기 버튼
        btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 완료 버튼
        btnDone.setOnClickListener {
            val titleInput = binding.etRecipeTitle.text.toString().trim()
            val descInput = binding.etRecipeDescription.text.toString().trim()
            val cookTime = binding.seekCookTime.progress

            if (titleInput.isBlank() || descInput.isBlank()) {
                Toast.makeText(requireContext(), "제목과 설명을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ingredientList = getIngredientList()
            val stepList = getCookingSteps()

            val newRecipe = Recipe(
                id = System.currentTimeMillis().toString(),
                title = titleInput,
                author = "테스트사용자",
                description = descInput,
                cookTimeMinutes = cookTime,
                coverImageUrl = null,
                ingredients = ingredientList,
                steps = stepList
            )

            viewModel.addRecipe(newRecipe)

            Toast.makeText(requireContext(), "테스트용 레시피 저장 완료!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }


    // 재료 추가 버튼
        binding.btnAddIngredient.setOnClickListener {
            addIngredientItem()
        }

        // 조리 방법 추가 버튼
        binding.btnAddSteps.setOnClickListener {
            addStepItem()
        }
    }

    private fun addIngredientItem() {
        val context = requireContext()

        val itemLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8.dp(context), 0, 0)
            }
        }

        // 삭제 아이콘
        val deleteButton = ImageView(context).apply {
            setImageResource(R.drawable.ic_cross_round)
            val size = 24.dp(context)
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                marginEnd = 8.dp(context)
                gravity = Gravity.CENTER_VERTICAL
            }
            imageAlpha = 128  // 약간 흐리게 표시
            setOnClickListener {
                binding.layoutIngredients.removeView(itemLayout)
            }
        }

        // 입력 필드
        val editText = EditText(context).apply {
            hint = "재료를 입력하세요."
            layoutParams = LinearLayout.LayoutParams(0, 48.dp(context), 1f)
            background = ContextCompat.getDrawable(context, R.drawable.edit_text_box)
            setPadding(32, 0, 32, 0)
        }

        itemLayout.addView(editText)
        itemLayout.addView(deleteButton)

        binding.layoutIngredients.addView(itemLayout)
    }

    fun Int.dp(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()

    private fun addStepItem() {
        val context = requireContext()
        val stepNumber = binding.layoutSteps.childCount + 1

        val outerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 12.dp(context), 0, 0)
            }
            tag = "step_container" // 삭제 후 재정렬할 때 식별용
        }

        // 상단: 순번 + 삭제 버튼
        val topLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val stepNumberText = TextView(context).apply {
            text = "Step $stepNumber"
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, 70, 1f)
        }

        val deleteButton = ImageView(context).apply {
            setImageResource(R.drawable.ic_cross_round)
            val size = 24.dp(context)
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                gravity = Gravity.CENTER_VERTICAL
            }
            imageAlpha = 128
            setOnClickListener {
                binding.layoutSteps.removeView(outerLayout)
                updateStepNumbers()
            }
        }

        topLayout.addView(stepNumberText)
        topLayout.addView(deleteButton)

        // 중간: 조리 설명
        val editText = EditText(context).apply {
            hint = "조리 방법을 입력하세요."
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                48.dp(context)
            ).apply {
                setMargins(0, 8.dp(context), 0, 0)
            }
            background = ContextCompat.getDrawable(context, R.drawable.edit_text_box)
            setPadding(32, 0, 32, 0)
        }

        // 최종 조립
        outerLayout.addView(topLayout)
        outerLayout.addView(editText)

        binding.layoutSteps.addView(outerLayout)
    }


    private fun updateStepNumbers() {
        for (i in 0 until binding.layoutSteps.childCount) {
            val container = binding.layoutSteps.getChildAt(i) as LinearLayout
            val topLayout = container.getChildAt(0) as LinearLayout
            val stepTextView = topLayout.getChildAt(0) as TextView
            stepTextView.text = "Step ${i + 1}"
        }
    }

    // 입력된 재료 리스트 추출 함수
    private fun getIngredientList(): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until binding.layoutIngredients.childCount) {
            val itemLayout = binding.layoutIngredients.getChildAt(i) as LinearLayout
            val editText = itemLayout.getChildAt(0) as EditText
            val ingredient = editText.text.toString().trim()
            if (ingredient.isNotEmpty()) list.add(ingredient)
        }
        return list
    }

    // 입력된 조리 방법 추출 함수
    private fun getCookingSteps(): List<CookingStep> {
        val steps = mutableListOf<CookingStep>()
        for (i in 0 until binding.layoutSteps.childCount) {
            val container = binding.layoutSteps.getChildAt(i) as LinearLayout
            val stepNumber = i + 1
            val editText = container.getChildAt(1) as EditText
            val description = editText.text.toString().trim()
            if (description.isNotEmpty()) {
                steps.add(CookingStep(stepNumber, description, null))
            }
        }
        return steps
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
