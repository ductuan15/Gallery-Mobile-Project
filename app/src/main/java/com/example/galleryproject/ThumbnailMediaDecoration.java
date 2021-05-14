package com.example.galleryproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class ThumbnailMediaDecoration extends RecyclerView.ItemDecoration {
    int numRow = 1;
    int posSpan = -1;
    public ThumbnailMediaDecoration(int numRow) {
        super();
        this.numRow = numRow;
    }


    @Override
    public void onDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        c.drawPaint(paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        c.drawText("Some Text", 10, 25, paint);
    }

    @Override
    public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        int column = ((GridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();

        if(pos == posSpan){
            outRect.right = (parent.getWidth() / numRow)  * ((numRow - 1) - column);
            outRect.left = (parent.getWidth() / numRow)  * (column - 1);
        }
    }

    public void setPosSpan(int posSpan) {
        this.posSpan = posSpan;
    }
}
