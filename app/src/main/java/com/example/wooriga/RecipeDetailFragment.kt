package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.wooriga.databinding.FragmentRecipeDetailBinding

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var recipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipe = arguments?.getParcelable("recipe") ?: error("레시피가 전달되지 않았습니다.")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 제목, 설명, 작성자, 시간
        binding.tvTitle.text = recipe.title
        binding.tvDescription.text = recipe.description
        binding.tvAuthor.text = recipe.author
        binding.tvTime.text = "조리시간 | ${recipe.cookTimeMinutes}분"

        // 대표 이미지
        Glide.with(this)
            .load(recipe.coverImageUrl)
            .placeholder(R.drawable.placeholder_image)
            .transform(RoundedCorners(10))
            .into(binding.ivCover)

        // 재료 리스트 동적 추가
        recipe.ingredients.forEach { ingredient ->
            val textView = TextView(requireContext()).apply {
                text = "✔ ${ingredient.name} ${ingredient.quantity}"
                textSize = 14f
                setPadding(0, 4, 0, 4)
            }
            binding.layoutIngredients.addView(textView)
        }

        // 조리 순서 동적 추가
        recipe.steps.forEach { step ->
            val stepText = TextView(requireContext()).apply {
                text = "${step.stepNumber}. ${step.description}"
                textSize = 14f
                setPadding(0, 8, 0, 8)
            }
            binding.layoutSteps.addView(stepText)

            step.imageUrl?.let { imageUrl ->
                val imageView = ImageView(requireContext()).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        400
                    )
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(imageView)
                binding.layoutSteps.addView(imageView)
            }
        }

        // 툴바
        val toolbar = view.findViewById<View>(R.id.custom_toolbar)
        val title = toolbar.findViewById<TextView>(R.id.tv_toolbar_title)
        val backBtn = toolbar.findViewById<View>(R.id.btn_back)

        title.text = "요리법"

        backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(recipe: Recipe): RecipeDetailFragment {
            return RecipeDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("recipe", recipe)
                }
            }
        }
    }
}
