package com.example.wooriga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.wooriga.databinding.FragmentRecipeDetailBinding
import androidx.appcompat.app.AlertDialog

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipeViewModel by activityViewModels()

    private var familyId: Long = -1
    private var recipeId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        familyId = arguments?.getLong("familyId") ?: -1
        recipeId = arguments?.getLong("recipeId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 툴바
        val toolbar = view.findViewById<View>(R.id.custom_toolbar)
        val title = toolbar.findViewById<TextView>(R.id.tv_toolbar_title)
        val backBtn = toolbar.findViewById<View>(R.id.btn_back)
        val btnMore = toolbar.findViewById<ImageButton>(R.id.btn_more)

        title.text = "요리법"
        backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        btnMore.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("요리법 삭제")
                .setMessage("이 요리법을 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    viewModel.deleteRecipe(
                        familyId = familyId,
                        recipeId = recipeId,
                        onSuccess = {
                            Toast.makeText(requireContext(), "삭제 완료", Toast.LENGTH_SHORT).show()
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        },
                        onFailure = {
                            Toast.makeText(requireContext(), "삭제 실패: $it", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                .setNegativeButton("취소", null)
                .show()
        }


        // 서버에서 요리법 상세 조회
        viewModel.loadRecipeDetail(
            familyId = familyId,
            recipeId = recipeId,
            onSuccess = { recipe ->
                showRecipe(recipe)
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), "불러오기 실패: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showRecipe(recipe: Recipe) {
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

        // 재료 리스트
        recipe.ingredients.forEach { ingredient ->
            val textView = TextView(requireContext()).apply {
                text = "✔ $ingredient"
                textSize = 14f
                setPadding(0, 4, 0, 4)
            }
            binding.layoutIngredients.addView(textView)
        }

        // 조리 순서
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(familyId: Long, recipeId: Long): RecipeDetailFragment {
            return RecipeDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong("familyId", familyId)
                    putLong("recipeId", recipeId)
                }
            }
        }
    }
}
